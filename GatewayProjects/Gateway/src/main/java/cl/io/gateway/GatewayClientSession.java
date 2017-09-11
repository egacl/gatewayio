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
 * This class represents a session for a channel that has established a network
 * connection with this gateway.
 *
 * @author egacl
 */
public class GatewayClientSession implements IGatewayClientSession {

    /**
     * client data
     */
    private final GatewayClient client;

    /**
     * gateway instance
     */
    private final IGateway gateway;

    /**
     * authentication status
     */
    private final AuthenticationStatus status;

    /**
     * network service connection origin
     */
    private final NetworkServiceSource origin;

    public GatewayClientSession(final GatewayClient client, final AuthenticationStatus status,
            final NetworkServiceSource origin) {
        this.gateway = null;
        this.client = client;
        this.status = status;
        this.origin = origin;
    }

    public GatewayClientSession(final IGateway gateway, GatewayClientSession source) {
        this.gateway = gateway;
        this.client = source.client;
        this.status = source.status;
        this.origin = source.origin;
    }

    /**
     * Sends a network message to this client.
     *
     * @param message
     *            network message
     * @throws NetworkDriverException
     *             if an errors ocurrs
     */
    @Override
    public <T> void send(NetworkMessage<T> message) throws NetworkDriverException {
        this.gateway.sendMessage(this, message, origin);
    }

    @Override
    public IGateway getGateway() {
        return this.gateway;
    }

    /**
     * @return cliente data
     */
    @Override
    public GatewayClient getClient() {
        return client;
    }

    /**
     *
     * @return authentication status
     */
    @Override
    public AuthenticationStatus getStatus() {
        return status;
    }

    /**
     *
     * @return network service connection origin
     */
    @Override
    public NetworkServiceSource getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GatewayClientSesion [client=");
        builder.append(client);
        builder.append(", gateway=");
        builder.append(gateway);
        builder.append(", status=");
        builder.append(status);
        builder.append(", origin=");
        builder.append(origin);
        builder.append("]");
        return builder.toString();
    }
}