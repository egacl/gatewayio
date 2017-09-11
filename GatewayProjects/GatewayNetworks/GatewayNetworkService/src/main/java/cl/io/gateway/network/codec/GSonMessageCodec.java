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
package cl.io.gateway.network.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.IEventMessageCodec;
import cl.io.gateway.network.driver.exception.NetworkMessageDeserializationException;
import cl.io.gateway.network.driver.exception.NetworkMessageSerializationException;

/**
 * Gson codec message implementation that transform messages object into json
 * string object.
 * 
 * @author egacl
 */
@SuppressWarnings("rawtypes")
public class GSonMessageCodec implements IEventMessageCodec<String> {

    private static final Logger logger = LoggerFactory.getLogger(GSonMessageCodec.class);

    private final Gson gson = new Gson();

    @Override
    public String serialize(NetworkMessage message) throws NetworkMessageSerializationException {
        return gson.toJson(message);
    }

    @Override
    public NetworkMessage deserealize(String protocolMessage) throws NetworkMessageDeserializationException {
        try {
            NetworkMessage message = gson.fromJson(protocolMessage, NetworkMessage.class);
            if (message == null) {
                throw new NetworkMessageDeserializationException("Json deserialization error, message is null?");
            }
            return message;
        } catch (JsonSyntaxException e) {
            logger.error("error processing message '" + protocolMessage + "'", e);
            throw new NetworkMessageDeserializationException("Json deserialization error", e);
        }
    }

    @Override
    public Class<String> protocolClass() {
        return String.class;
    }
}
