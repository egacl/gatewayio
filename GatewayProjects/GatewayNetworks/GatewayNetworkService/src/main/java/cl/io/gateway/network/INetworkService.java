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

import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.network.handler.INetworkEventListener;
import cl.io.gateway.network.handler.INetworkMessageHandler;

/**
 * Network messaging service interface, entity designed to fully abstract
 * network concepts such as protocols, serialization, deserealization and
 * provide a paradigm oriented to sending and receiving objects from listeners.
 *
 * @see NetworkService
 * @see NetworkConfiguration
 * @author egacl
 */
@SuppressWarnings("rawtypes")
public interface INetworkService {

    /**
     * Network messaging service internal initilization and starting.
     *
     * @return This network messaging service instance.
     * @throws Exception
     *             When initialization exception occurs.
     */
    INetworkService start() throws Exception;

    /**
     * Stop this network messaging service.
     */
    void stop();

    /**
     * Allows network message handler registration. Handler is associated to an
     * event name that represent a network message type or structure.
     *
     * @param event
     *            Event name that represent a network message type or structure.
     * @param handler
     *            Network message handler to received <code>event</code> messages.
     * @param <T>
     *            Message object type.
     * @return Return <code>true</code> when message handler was registered.
     */
    <T> boolean addMessageHandler(String event, INetworkMessageHandler<T> handler);

    /**
     * Deregister a network message handler.
     *
     * @param event
     *            Event name that represent a network message type or structure.
     * @return Return <code>true</code> if handler was removed.
     */
    INetworkMessageHandler removeMessageHandler(String event);

    /**
     * Allows network event handler registration. Handler allows to listen the
     * channel's network status.
     *
     * @param handler
     *            Network event handler.
     */
    void addNetworkEventHandler(INetworkEventListener handler);

    /**
     * Deregister a network event handler.
     *
     * @param handler
     *            Network event handler.
     * @return Return <code>true</code> if handler was removed.
     */
    boolean removeNetworkEventHandler(INetworkEventListener handler);

    /**
     * Verify if exists any handler for an event.
     *
     * @param event
     *            to search
     * @return Return <code>true</code> if handler was exists.
     */
    boolean hasNetworkEventHandlerFor(String event);

    /**
     * Allows to send network message to an specific channel.
     *
     * @param channelId
     *            Channel identifier.
     * @param message
     *            Network message.
     * @param <T>
     *            Network message object type.
     * @throws NetworkDriverException
     *             When sending network message fails (channel doesn't exists).
     */
    <T> void send(String channelId, NetworkMessage<T> message) throws NetworkDriverException;

    /**
     * Allows to send network message to an specific channel.
     *
     * @param channelId
     *            Channel identifier.
     * @param message
     *            Network message.
     * @param <T>
     *            Network message object type.
     * @param deliveryStatus
     *            Asynchronous listener for message delivery status.
     * @throws NetworkDriverException
     *             When sending network message fails (channel doesn't exists).
     */
    <T> void send(String channelId, NetworkMessage<T> message, IDeliveryStatusListener deliveryStatus)
            throws NetworkDriverException;

    /**
     * Allows to broadcast a message to all connected channels.
     *
     * @param message
     *            Network message.
     * @param <T>
     *            Network message object type.
     * @throws NetworkDriverException
     *             When sending network message fails.
     */
    <T> void broadcast(NetworkMessage<T> message) throws NetworkDriverException;

    /**
     * Allows to establish a network connection with another channel.
     *
     * @param connData
     *            Network connection data.
     */
    void connectTo(NetworkConnection connData);

    /**
     * Allows to establish a network connection with another channel.
     *
     * @param connData
     *            Network connection data.
     * @param connStatus
     *            Asynchronous listener for connections status.
     */
    void connectTo(NetworkConnection connData, IConnectionStatus connStatus);
}
