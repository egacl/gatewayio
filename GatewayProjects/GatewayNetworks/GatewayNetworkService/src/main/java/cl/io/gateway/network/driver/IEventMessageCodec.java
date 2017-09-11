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

import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkMessageDeserializationException;
import cl.io.gateway.network.driver.exception.NetworkMessageSerializationException;

/**
 * Interface that allows you to define how to serialize and deserialize java
 * objects. This operation is performed by the network driver when receiving and
 * sending messages as appropriate.
 *
 * @author egacl
 * @param <P>
 *            Network protocol type.
 */
@SuppressWarnings("rawtypes")
public interface IEventMessageCodec<P extends Object> {

    /**
     * Allows you to transform an object to an object type that can be processed by
     * the network driver.
     *
     * @param message
     *            java object message
     * @return object to process by the network driver
     * @throws NetworkMessageSerializationException
     */
    P serialize(NetworkMessage message) throws NetworkMessageSerializationException;

    /**
     * Allows you to transform an network driver object to an object type that can
     * be processed by java applications.
     * 
     * @param protocolMessage
     *            protocol message object
     * @return object to process by java applications
     * @throws NetworkMessageDeserializationException
     */
    NetworkMessage deserealize(P protocolMessage) throws NetworkMessageDeserializationException;

    Class<P> protocolClass();
}
