/*
 * Copyright 2017 GetSoftware (http://www.getsoftware.cl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cl.io.gateway.network.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.network.IConnectionStatus;
import cl.io.gateway.network.IDeliveryStatusListener;
import cl.io.gateway.network.IServiceDriverCommunication;
import cl.io.gateway.network.NetworkConfiguration;
import cl.io.gateway.network.NetworkConnection;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.network.driver.exception.NetworkMessageDeserializationException;
import cl.io.gateway.network.driver.exception.NetworkMessageSerializationException;
import cl.io.gateway.stats.Counter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Abstract class representing a network driver. It has all the necessary
 * structures to interact with the network messaging service, server module
 * implementation, network client management, connected channel management and
 * statistics accounting.
 *
 * This class is adapted to contain and work with instances of the netty
 * framework.
 *
 * @see INetworkDriver
 * @author egacl
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractNetworkDriver implements INetworkDriver {

    private static final Logger logger = LoggerFactory.getLogger(AbstractNetworkDriver.class);

    private static final String STATS_COUNTER_INPUT_MSG_PREFIX = "InputMsgEventCounterOK.";

    private static final String STATS_COUNTER_OUTPUT_MSG_PREFIX = "OutputMsgEventCounterOK.";

    private static final String STATS_COUNTER_INPUT_MSG_PREFIX_ERROR = "InputMsgEventCounterProcessERROR.";

    private static final String STATS_COUNTER_OUTPUT_MSG_PREFIX_ERROR = "OutputMsgEventCounterERROR.";

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workerGroup;

    /**
     * Bridge between the network driver and the messaging service
     */
    private final IServiceDriverCommunication IServiceDriverCommunication;

    /**
     * Channels connected map
     */
    private final ConcurrentHashMap<String, DriverChannel> channelsMap;

    /**
     * Driver network configuration
     */
    private final NetworkConfiguration configuration;

    /**
     * Codec message procesor map
     */
    private final ConcurrentHashMap<Class, IEventMessageCodec> eventMessageCodecMap;

    /**
     * Server instance for client connection piping
     */
    private INetworkDriverServer networkServer;

    /**
     * Client instance for channeling connections to other servers on the network
     */
    private INetworkDriverClientManager clientManager;

    /**
     * Start flag
     */
    private AtomicBoolean start = new AtomicBoolean(false);

    /**
     * Pipe sending messages to the network
     */
    private IDriverChannelOutboundHandler outboundHandler;

    /**
     * Messages statistics counter instance
     */
    private final Counter messageCounter = new Counter();

    /**
     * It allows to attach a counter for the collection of statistics (optional)
     */
    private Counter attachedMessageCounter = null;

    @Override
    public void attachCounter(Counter counter) {
        this.attachedMessageCounter = counter;
    }

    public AbstractNetworkDriver(final NetworkConfiguration configuration,
            final IServiceDriverCommunication IServiceDriverCommunication, final EventLoopGroup bossGroup,
            final EventLoopGroup workerGroup) {
        this.configuration = configuration;
        this.IServiceDriverCommunication = IServiceDriverCommunication;
        this.channelsMap = new ConcurrentHashMap<>(50, 0.5f);
        this.eventMessageCodecMap = new ConcurrentHashMap<>(50, 0.5f);
        if (this.configuration.getCodecs() != null) {
            for (IEventMessageCodec codec : this.configuration.getCodecs()) {
                final IEventMessageCodec mapCodec = this.eventMessageCodecMap.get(codec.protocolClass());
                if (mapCodec != null) {
                    logger.warn("It already exists another codec for " + codec.protocolClass() + " class");
                } else {
                    this.eventMessageCodecMap.put(codec.protocolClass(), codec);
                }
            }
        } else {
            logger.error("No exists event message codec?");
            throw new IllegalStateException("IEventMessageCodecs not found");
        }
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    @Override
    public void initialice() {
        if (this.networkServer == null) {
            logger.info("Websocket driver server initilization");
            if (this.configuration.isAcceptClients()) {
                this.networkServer = this.createServer();
                this.networkServer.initialice();
            }
        }
        if (this.clientManager == null) {
            logger.info("Websocket driver connector initilization");
            this.clientManager = this.createClient();
            this.clientManager.initialice();
        }
        if (this.outboundHandler == null) {
            logger.info("Websocket driver outbound handler initilization");
            this.outboundHandler = this.createOutBoundHandler();
        }
    }

    @Override
    public void start() {
        if (start.get()) {
            logger.warn("Websocket driver already started");
            return;
        }
        logger.info("Websocket driver start");
        start.set(true);
        if (this.configuration.isAcceptClients()) {
            this.networkServer.start();
        }
        this.clientManager.start();
    }

    @Override
    public void stop() {
        if (start.get()) {
            logger.info("Websocket driver stop");
            if (this.configuration.isAcceptClients()) {
                this.networkServer.stop();
            }
            this.clientManager.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return start.get();
    }

    @Override
    public boolean existsChannel(String channelId) {
        return this.channelsMap.containsKey(channelId);
    }

    @Override
    public void connectTo(NetworkConnection connection, final IConnectionStatus connStatus) {
        this.clientManager.connect(connection, connStatus);
    }

    @Override
    public String[] getAvailableChannels() {
        return this.channelsMap.keySet().toArray(new String[0]);
    }

    @Override
    public void closeChannel(final String channelId, final IConnectionStatus connStatus) {
        final DriverChannel channel = this.channelsMap.get(channelId);
        if (channel == null) {
            connStatus.error(channelId, new NetworkDriverException("Unknown channel: " + channelId));
            return;
        }
        // Se obtiene el canal y se cierra la conexion
        channel.getChannel().close().addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess())
                    connStatus.success(channelId);
                else
                    connStatus.error(channelId, future.cause());
            }
        });
    }

    @Override
    public void send(final String channelId, final NetworkMessage message) throws NetworkDriverException {
        this.send(channelId, message, null);
    }

    @Override
    public void send(final String channelId, final NetworkMessage message, final IDeliveryStatusListener deliveryStatus)
            throws NetworkDriverException {
        // Se obtiene el canal al cual se va a enviar el mensaje
        final DriverChannel channel = this.channelsMap.get(channelId);
        if (channel == null) {
            throw new NetworkDriverException("Unknown channel: " + channelId);
        }
        try {
            this.outboundHandler.write(message, channel, deliveryStatus);
        } catch (Exception err) {
            throw new NetworkDriverException("Error sending message to '" + channelId + "'", err);
        }
    }

    @Override
    public void broadcast(NetworkMessage message) throws NetworkDriverException {
        final List<DriverChannel> channels = new ArrayList<>(this.channelsMap.values());
        for (final DriverChannel channel : channels) {
            // Se envia el mensaje
            try {
                this.outboundHandler.write(message, channel);
            } catch (Exception err) {
                throw new NetworkDriverException("Error sending message to '" + channel + "'", err);
            }
        }
    }

    /**
     * It allows you to process an incoming message from the network, add context
     * data and deliver it to the network messaging service.
     *
     * @param channelId
     *            channel that sends the message
     * @param message
     *            message received
     */
    public <T> void onNetworkMessage(final String channelId, final NetworkMessage<T> message) {
        try {
            // se aumenta la secuencia de mensajes del cliente y se entrega como dato de
            // contexto
            message.putContext(NetworkMessage.CHANNEL_MESSAGE_SEQUENCE,
                    this.channelsMap.get(channelId).addAndGetMessageCounter());
            // se aumenta la secuencia de mensajes asociados al evento recibido
            message.putContext(NetworkMessage.EVENT_MESSAGE_SEQUENCE,
                    this.countReceivedEvent(message.getEvent(), true));
            // Se setea el channelId
            message.setChannelId(channelId);
            this.IServiceDriverCommunication.onNetworkMessage(message);
        } catch (NetworkDriverException err) {
            this.countReceivedEvent(message.getEvent(), false);
            logger.error("Error processing message " + message, err);
        }
    }

    /**
     * Allows receiving a pong message and notifies a network event to the network
     * messaging service.
     *
     * @param ctx
     *            channel context
     * @param channelId
     *            channel that sends the pong message
     */
    public void pongReceived(final ChannelHandlerContext ctx, final String channelId) {
        // Se procesa evento de hearbeat
        logger.trace(channelId + ": Ping response received");
        // Se resetea el contador de timeout
        final DriverChannel driverChannel = this.getDriverChannel(channelId);
        if (driverChannel != null) {
            driverChannel.resetReconnectCounter();
            logger.info("Channel " + channelId + " " + NetworkEventType.TIMEOUT_ALERT_OFF);
            this.onNetworkEvent(channelId, ctx.channel(), NetworkEventType.TIMEOUT_ALERT_OFF);
        }
    }

    /**
     * It allows you to process all network events, register or unregister connected
     * channels and notify these events to the network messaging service.
     *
     * @param channelId
     *            channel
     * @param channel
     *            channel
     * @param eventType
     *            network event
     */
    public void onNetworkEvent(final String channelId, final Channel channel, final NetworkEventType eventType) {
        final NetworkEvent networkEvent = new NetworkEvent(channelId, eventType);
        if (eventType == NetworkEventType.ACTIVE) {
            this.channelsMap.put(channelId, new DriverChannel(channelId, channel));
        } else if (eventType == NetworkEventType.INACTIVE) {
            this.channelsMap.remove(channelId);
        }
        try {
            this.IServiceDriverCommunication.onNetworkEvent(networkEvent);
        } catch (Throwable err) {
            logger.error("Error procesing network event " + networkEvent, err);
        }
    }

    /**
     * Get channel instance data
     *
     * @param channelId
     *            channel
     * @return channel instance
     */
    public DriverChannel getDriverChannel(final String channelId) {
        return this.channelsMap.get(channelId);
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    /**
     * Get network configuration
     *
     * @return network configuration
     */
    public NetworkConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * A method for serializing a message so that it can be sent over the network.
     *
     * @param protocolClass
     *            Network protocol objecto type
     * @param msg
     *            message to serialize
     * @return protocol message instance
     * @throws NetworkMessageSerializationException
     *             if an error ocurrs
     */
    @SuppressWarnings("unchecked")
    public <P> P serialize(final Class<P> protocolClass, final NetworkMessage msg)
            throws NetworkMessageSerializationException {
        final IEventMessageCodec<P> codec = this.eventMessageCodecMap.get(protocolClass);
        if (codec == null) {
            logger.error("erialization codec not found for %s class", protocolClass.getName());
            throw new NetworkMessageSerializationException("Codec not found for " + protocolClass.getName());
        }
        return codec.serialize(msg);
    }

    /**
     * A method for deserializing a message so that it can be sent to network
     * messaging service.
     *
     * @param protocolClass
     *            Network protocol objecto type
     * @param protocolMessage
     *            Network protocol message instance
     * @return java object instance representation for the message
     * @throws NetworkMessageDeserializationException
     *             if an error ocurrs
     */
    @SuppressWarnings("unchecked")
    public <P> NetworkMessage deserealize(final Class<P> protocolClass, final P protocolMessage)
            throws NetworkMessageDeserializationException {
        final IEventMessageCodec<P> codec = this.eventMessageCodecMap.get(protocolClass);
        if (codec == null) {
            logger.error("Deserealization codec not found for '" + protocolClass.getSimpleName()
                    + "' class and message '" + protocolMessage + "'");
            throw new NetworkMessageDeserializationException("Codec not found for '" + protocolClass.getSimpleName()
                    + "' class and message '" + protocolMessage + "'");
        }
        return codec.deserealize(protocolMessage);
    }

    /**
     * Instance for channeling connections to other servers on the network.
     *
     * @return instance for channeling connections to other servers on the network
     */
    public INetworkDriverClientManager getClientManager() {
        return clientManager;
    }

    /**
     * Server instance for client connection piping
     *
     * @return instance for client connection piping
     */
    public INetworkDriverServer getNetworkServer() {
        return networkServer;
    }

    /**
     * Method that allows to implement the logic necessary for the creation of a
     * server for sending and receiving messages and network connections through
     * some protocol.
     *
     * @return server instance
     */
    public abstract INetworkDriverServer createServer();

    /**
     * Method that allows to implement the logic necessary for the creation of a
     * clients manager for sending and receiving messages and and make connections
     * to other network servers using a protocol.
     *
     * @return clients manager instance
     */
    public abstract INetworkDriverClientManager createClient();

    /**
     * A method that allows you to create an instance for the processing of messages
     * that are sent over the network.
     *
     * @return ChannelOutboundHandler instance
     */
    public abstract IDriverChannelOutboundHandler createOutBoundHandler();

    /**
     * Allows to define an unique identifier for an specific channel.
     *
     * @param channel
     *            channel
     * @return unique identifier
     */
    public abstract String getChannelId(final Channel channel);

    /**
     * Method that takes statistics of messages sent over the network.
     *
     * @param event
     *            event message
     * @param ok
     *            true sended without error
     * @return event message quantity sended by the driver
     */
    public long countSendEvent(final String event, boolean ok) {
        String key;
        long value;
        if (ok) {
            key = STATS_COUNTER_OUTPUT_MSG_PREFIX + event;
            value = this.messageCounter.increment(STATS_COUNTER_OUTPUT_MSG_PREFIX + event);
        } else {
            key = STATS_COUNTER_OUTPUT_MSG_PREFIX_ERROR + event;
            value = this.messageCounter.increment(STATS_COUNTER_OUTPUT_MSG_PREFIX_ERROR + event);
        }
        if (attachedMessageCounter != null) {
            attachedMessageCounter.setValue(key, value);
        }
        return value;
    }

    /**
     * Method that takes statistics of messages received over the network.
     *
     * @param event
     *            event message
     * @param ok
     *            true received without error
     * @return event message quantity sended by the driver
     */
    public long countReceivedEvent(final String event, boolean ok) {
        String key;
        long value;
        if (ok) {
            key = STATS_COUNTER_INPUT_MSG_PREFIX + event;
            value = this.messageCounter.increment(STATS_COUNTER_INPUT_MSG_PREFIX + event);
        } else {
            key = STATS_COUNTER_INPUT_MSG_PREFIX_ERROR + event;
            value = this.messageCounter.increment(STATS_COUNTER_INPUT_MSG_PREFIX_ERROR + event);
        }
        if (attachedMessageCounter != null) {
            attachedMessageCounter.setValue(key, value);
        }
        return value;
    }
}
