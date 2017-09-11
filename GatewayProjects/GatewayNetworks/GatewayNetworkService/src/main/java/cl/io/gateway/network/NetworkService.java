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
package cl.io.gateway.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.network.driver.INetworkDriver;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.network.handler.INetworkEventListener;
import cl.io.gateway.network.handler.INetworkMessageHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Class of messaging service, acts as a bridge to communicate the applications
 * with the network driver and perform I / O operations abstracting from the
 * protocol used.
 *
 * @author egacl
 */
@SuppressWarnings("rawtypes")
public class NetworkService implements INetworkService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkService.class);

    /**
     * Boss thread pool
     */
    private final EventLoopGroup bossGroup;

    /**
     * Worker threadpool
     */
    private final EventLoopGroup workerGroup;

    /**
     * Map that contains message listener to notify java applications
     */
    private final ConcurrentHashMap<String, INetworkMessageHandler<?>> messagesHandlerMap;

    /**
     * List that contains network event listener to notify java application
     */
    private final List<INetworkEventListener> networkEventsHandlerList;

    /**
     * Network driver instance for low level protocol operations
     */
    private INetworkDriver networkDriver;

    /**
     * Network configuration
     */
    private final NetworkConfiguration configuration;

    public NetworkService(final NetworkConfiguration configuration) {
        this(configuration, new NioEventLoopGroup(), new NioEventLoopGroup());
    }

    public NetworkService(final NetworkConfiguration configuration, final EventLoopGroup bossGroup,
            final EventLoopGroup workerGroup) {
        this.configuration = configuration;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.messagesHandlerMap = new ConcurrentHashMap<>(50, 0.5F);
        this.networkEventsHandlerList = new ArrayList<>(50);
    }

    private INetworkService initialice() throws Exception {
        if (this.networkDriver == null) {
            logger.info("Network service initilization");
            // Se crea instancia del driver de red
            this.networkDriver = this.configuration.getDriverClass()
                    .getConstructor(NetworkConfiguration.class, IServiceDriverCommunication.class, EventLoopGroup.class,
                            EventLoopGroup.class)
                    .newInstance(this.configuration, this.createDriverCommunication(), this.bossGroup,
                            this.workerGroup);
            // Se inicializa el driver de red
            this.networkDriver.initialice();
        }
        return this;
    }

    @Override
    public INetworkService start() throws Exception {
        logger.info("Network service start");
        this.initialice();
        this.networkDriver.start();
        return this;
    }

    @Override
    public void stop() {
        logger.info("Network service stop");
        this.networkDriver.stop();
    }

    @Override
    public <T> boolean addMessageHandler(String event, INetworkMessageHandler<T> handler) {
        INetworkMessageHandler previus = this.messagesHandlerMap.get(event);
        if (previus == null) {
            this.messagesHandlerMap.put(event, handler);
            return true;
        }
        return false;
    }

    @Override
    public INetworkMessageHandler removeMessageHandler(String event) {
        return this.messagesHandlerMap.remove(event);
    }

    @Override
    public void addNetworkEventHandler(INetworkEventListener handler) {
        synchronized (this.networkEventsHandlerList) {
            this.networkEventsHandlerList.add(handler);
        }
    }

    @Override
    public boolean removeNetworkEventHandler(INetworkEventListener handler) {
        synchronized (this.networkEventsHandlerList) {
            return this.networkEventsHandlerList.remove(handler);
        }
    }

    @Override
    public boolean hasNetworkEventHandlerFor(String event) {
        return this.messagesHandlerMap.containsKey(event);
    }

    @Override
    public <T> void send(final String channelId, final NetworkMessage<T> message) throws NetworkDriverException {
        this.networkDriver.send(channelId, message);
    }

    @Override
    public <T> void send(String channelId, NetworkMessage<T> message, IDeliveryStatusListener deliveryStatus)
            throws NetworkDriverException {
        this.networkDriver.send(channelId, message, deliveryStatus);
    }

    @Override
    public <T> void broadcast(NetworkMessage<T> message) throws NetworkDriverException {
        this.networkDriver.broadcast(message);
    }

    private IServiceDriverCommunication createDriverCommunication() {
        return new IServiceDriverCommunication() {

            @Override
            public <T> void onNetworkMessage(final NetworkMessage<T> message) throws NetworkDriverException {
                onMessage(message);
            }

            @Override
            public void onNetworkEvent(final NetworkEvent event) {
                broadcastNetworkEvent(event);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T> void onMessage(final NetworkMessage<T> message) throws NetworkDriverException {
        final INetworkMessageHandler<T> handler = (INetworkMessageHandler<T>) this.messagesHandlerMap
                .get(message.getEvent());
        if (handler != null) {
            try {
                handler.onMessage(message);
            } catch (Exception e) {
                throw new NetworkDriverException("Error processing message: " + message, e);
            }
        }
    }

    private void broadcastNetworkEvent(final NetworkEvent event) {
        synchronized (this.networkEventsHandlerList) {
            for (final INetworkEventListener eventHandler : this.networkEventsHandlerList) {
                try {
                    eventHandler.onEvent(event);
                } catch (Throwable err) {
                    logger.error("Error reporting network status event", err);
                }
            }
        }
    }

    @Override
    public void connectTo(NetworkConnection connData) {
        this.networkDriver.connectTo(connData, null);
    }

    @Override
    public void connectTo(NetworkConnection connData, IConnectionStatus connStatus) {
        this.networkDriver.connectTo(connData, connStatus);
    }
}
