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
package cl.io.gateway;

import cl.io.gateway.auth.AuthenticationStatus;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.vo.GatewayClient;

/**
 * This interface represents a session for a channel that has established a
 * network connection with this gateway.
 * 
 * @author egacl
 *
 */
public interface IGatewayClientSession {

    /**
     * Sends a network message to this client.
     *
     * @param message
     *            network message
     * @throws NetworkDriverException
     *             if an errors ocurrs
     */
    public <T> void send(NetworkMessage<T> message) throws NetworkDriverException;

    /**
     *
     * @return gateway instance
     */
    public IGateway getGateway();

    /**
     * @return cliente data
     */
    public GatewayClient getClient();

    /**
     *
     * @return authentication status
     */
    public AuthenticationStatus getStatus();

    /**
     *
     * @return network service connection origin
     */
    public NetworkServiceSource getOrigin();
}
