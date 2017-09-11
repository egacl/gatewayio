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
import cl.io.gateway.vo.GatewayClient;

public class InternalGatewaySession {

    private final GatewayClient client;

    private IGateway gateway;

    private AuthenticationStatus status;

    public InternalGatewaySession(final GatewayClient client) {
        this.client = client;
    }

    /**
     * @return the client
     */
    public GatewayClient getClient() {
        return client;
    }

    public AuthenticationStatus getStatus() {
        return status;
    }

    public void setStatus(AuthenticationStatus status) {
        this.status = status;
    }

    public GatewayClientSession toPublicSession(NetworkServiceSource origin) {
        return new GatewayClientSession(client, status, origin);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GatewayClientSesion [client=").append(client).append(", gateway=").append(gateway)
                .append(", status=").append(status).append("]");
        return builder.toString();
    }
}
