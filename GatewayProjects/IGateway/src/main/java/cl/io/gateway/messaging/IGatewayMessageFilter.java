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
 * Interface that allows defining a message filter for the gateway. The filter
 * is applied on one event either during reception or sending process.
 *
 * @see GatewayMessageFilter
 * @author egacl
 *
 * @param <T>
 *            Object type contained in the network message to be filtered
 */
public interface IGatewayMessageFilter<T> {

    /**
     * Allows you to filter an incoming message before it is processed by a gateway
     * service. The message passes by value for each of the filters, this means that
     * each of the changes made on the message parameter will be delivered to the
     * following filters and finally to the gateway service.
     *
     * @param message
     *            message to be filtered
     * @param session
     *            session that sent the message
     * @return true true if the message meets the criteria of the filter and false
     *         otherwise
     * @throws Exception
     *             if an error ocurrs
     */
    boolean doFilterRequest(final NetworkMessage<T> message, final IGatewayClientSession session) throws Exception;

    /**
     * Allows you to filter an outgoing message after it has been processed by a
     * gateway service. The message passes by value for each of the filters, this
     * means that each of the changes made on the message parameter will be
     * delivered to the following filters and finally to the network driver.
     *
     * @param message
     *            message to be filtered
     * @param session
     *            session that sent the message
     * @return true if the message meets the criteria of the filter and false
     *         otherwise
     * @throws Exception
     *             if an error ocurrs
     */
    boolean doFilterResponse(final NetworkMessage<T> message, final IGatewayClientSession session) throws Exception;

    /**
     * It allows to perform some action in case of an error occurred during an input
     * filter or output of the received message as parameter.
     *
     * @param message
     *            message to be filtered
     * @param session
     *            session that sent the message
     * @param err
     *            Exception occurred during filter processing
     * @throws Exception
     *             If another error occurs during the execution of this method
     */
    void onError(final NetworkMessage<T> message, final IGatewayClientSession session, Throwable err) throws Exception;
}
