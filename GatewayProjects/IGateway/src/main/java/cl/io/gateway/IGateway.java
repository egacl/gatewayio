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

import java.io.IOException;

import cl.io.gateway.exception.GatewayProcessException;
import cl.io.gateway.messaging.IGatewayMessageHandler;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.properties.XProperties;

/**
 * Interface that allows gateway services to perform network operations, answer
 * and respond to messages, access plugins and other functionality of the
 * framework.
 *
 * @author egacl
 *
 */
public interface IGateway {

    <T> void addMessageHandler(String event, IGatewayMessageHandler<T> handler) throws GatewayProcessException;

    void removeMessageHandler(String event) throws GatewayProcessException;

    <T> void sendMessage(IGatewayClientSession client, NetworkMessage<T> message, NetworkServiceSource... origin)
            throws NetworkDriverException;

    XProperties getProperties(String propertyFileName) throws IOException;

    <T> T getPlugin(String pluginId, Class<T> pluginType);
}
