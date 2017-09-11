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
 * Interface that allows to define a network message handler.
 *
 * @author egacl
 * @param <T>
 *            Object type contained in the network message to be filtered
 */
public interface IGatewayMessageHandler<T> {

    /**
     * It allows you to process a message received from the network. This method
     * receives the received message and its corresponding session.
     *
     * @param message
     *            message to be processed
     * @param session
     *            session that sent the message
     * @exception Exception
     *                if an error ocurrs
     */
    void onMessage(final NetworkMessage<T> message, final IGatewayClientSession session) throws Exception;
}
