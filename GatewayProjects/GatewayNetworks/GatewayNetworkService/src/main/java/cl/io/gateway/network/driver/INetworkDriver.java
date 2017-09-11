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

import cl.io.gateway.network.IConnectionStatus;
import cl.io.gateway.network.IDeliveryStatusListener;
import cl.io.gateway.network.NetworkConnection;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.stats.Counter;

/**
 * Interface that allows communication with the network driver which performs
 * low-level network operations for sending messages and establishing
 * connections.
 *
 * @author egacl
 */
@SuppressWarnings("rawtypes")
public interface INetworkDriver {

    /**
     * Send a message to a specific channel.
     *
     * @param channelId
     *            channel
     * @param message
     *            message to send
     * @throws NetworkDriverException
     *             if any error occurs
     */
    void send(String channelId, NetworkMessage message) throws NetworkDriverException;

    /**
     * Send a message to a specific channel. The IDeliveryStatus instance acts as a
     * callback for the driver to communicate when the message was actually sent
     * over the network.
     *
     * @param channelId
     *            channel
     * @param message
     *            message to send
     * @param deliveryStatus
     *            DeliveryStatus instance
     * @throws NetworkDriverException
     *             if any error occurs
     */
    void send(String channelId, NetworkMessage message, IDeliveryStatusListener deliveryStatus) throws NetworkDriverException;

    /**
     * It allows to distribute the message to all the channels registered in the
     * network.
     *
     * @param message
     *            message to send
     * @throws NetworkDriverException
     *             if any error occurs
     */
    void broadcast(NetworkMessage message) throws NetworkDriverException;

    /**
     * Allows you to initialize the network driver.
     */
    void initialice();

    /**
     * Allows you to run the network driver.
     */
    void start();

    /**
     * Allows you to stop the network driver.
     */
    void stop();

    /**
     * Returns true if the network driver is running and false otherwise.
     *
     * @return Returns true if the network driver is running and false otherwise
     */
    boolean isRunning();

    /**
     * Returns true if the channel is registered or connected to the network.
     *
     * @param channelId
     *            channel
     * @return Returns true if the channel is registered or connected to the network
     */
    boolean existsChannel(String channelId);

    /**
     * Disconnect a channel from the network. The IConnectionStatus instance acts as
     * a callback for the driver to communicate when the channel was actually
     * disconnected.
     *
     * @param channelId
     *            channel
     * @param connStatus
     *            IConnectionStatus instance
     */
    void closeChannel(String channelId, IConnectionStatus connStatus);

    /**
     * Sets the connection to a network channel. The IConnectionStatus instance acts
     * as a callback for the driver to communicate when the channel was actually
     * connected.
     *
     * @param connection
     * @param connStatus
     */
    void connectTo(NetworkConnection connection, IConnectionStatus connStatus);

    /**
     * List all channels connected to the network.
     *
     * @return List all channels connected to the network
     */
    String[] getAvailableChannels();

    /**
     * Allows you to attach a counter for collecting statistics from the network
     * driver.
     *
     * @param counter
     *            counter stats instance
     */
    void attachCounter(Counter counter);
}
