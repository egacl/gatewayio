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
import cl.io.gateway.network.NetworkMessage;

/**
 * This class allows to contain a network message and the sending session of
 * this one.
 *
 * @see GatewayMessageHandler
 * @author egacl
 *
 * @param <T>
 *            netwrok message type
 */
public class GatewayMessageContext<T> {

    /**
     * network message
     */
    private final NetworkMessage<T> message;

    /**
     * gateway session that sent the message
     */
    private final IGatewayClientSession clientSession;

    public GatewayMessageContext(final NetworkMessage<T> message, final IGatewayClientSession clientSession) {
        this.message = message;
        this.clientSession = clientSession;
    }

    /**
     *
     * @return network message
     */
    public NetworkMessage<T> getMessage() {
        return message;
    }

    /**
     *
     * @return gateway session that sent the message
     */
    public IGatewayClientSession getClientSession() {
        return clientSession;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GatewayMessageContext [message=");
        builder.append(message);
        builder.append(", clientSession=");
        builder.append(clientSession);
        builder.append("]");
        return builder.toString();
    }
}
