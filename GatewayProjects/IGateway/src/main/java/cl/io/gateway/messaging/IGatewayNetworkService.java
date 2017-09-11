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
package cl.io.gateway.messaging;

import cl.io.gateway.IGatewayClientSession;
import cl.io.gateway.auth.IAuthenticationStatusListener;
import cl.io.gateway.exception.GatewayProcessException;
import cl.io.gateway.network.IDeliveryStatusListener;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;

/**
 * This interface represents the network service of the gateway, which allows to
 * perform operations of sending and receiving messages and interact with the
 * connected sessions.
 *
 * @author egacl
 *
 */
public interface IGatewayNetworkService {

    /**
     * This method allows adding a handler or listener for a particular event.
     *
     * @param event
     *            event identifier
     * @param handler
     *            handler or listener to receive messages
     * @throws GatewayProcessException
     *             if an error occurs
     */
    <T> void addMessageHandler(final String event, final IGatewayMessageHandler<T> handler)
            throws GatewayProcessException;

    /**
     * This method allows removing a handler or listener for a particular event.
     *
     * @param event
     *            event identifier
     */
    void removeMessageHandler(final String event);

    /**
     * This method allows sending a message for an specific session.
     *
     * @param session
     *            session to sent the message
     * @param message
     *            message to be sended
     * @throws NetworkDriverException
     *             if an error occurs
     */
    <T> void sendNetworkMessage(final IGatewayClientSession session, final NetworkMessage<T> message)
            throws NetworkDriverException;

    /**
     * This method allows sending a message for an specific session. A callback
     * delivery is received to get notification when the message is sent over the
     * network.
     *
     * @param session
     *            session to sent the message
     * @param message
     *            message to be sended
     * @param deliveryStatus
     *            delivery status listener.
     * @throws NetworkDriverException
     *             if an error occurs
     */
    <T> void sendNetworkMessage(final IGatewayClientSession session, final NetworkMessage<T> message,
            final IDeliveryStatusListener deliveryStatus) throws NetworkDriverException;

    /**
     * This method allows you to add a listener to get status change updates on
     * sessions connected to the gateway.
     *
     * @param listener
     *            listener instance
     */
    void addAuthenticationStatusListener(IAuthenticationStatusListener listener);

    /**
     * This method allows you to delete a session state change listener.
     *
     * @param listener
     *            listener instance
     * @return true if the listener was deleted and false otherwise
     */
    boolean removeAuthenticationStatusListener(IAuthenticationStatusListener listener);
}
