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
package cl.io.gateway.network;

import cl.io.gateway.network.driver.exception.NetworkDriverException;

/**
 * Interface that allows the transmission of messages and connection events from
 * the specific network driver to the network messaging service.
 *
 * @author egacl
 */
public interface IServiceDriverCommunication {

    /**
     * This method is called when a network message is received.
     *
     * @param message
     *            Network message.
     * @param <T>
     *            Message object type.
     */
    <T> void onNetworkMessage(NetworkMessage<T> message) throws NetworkDriverException;

    /**
     * This method is called when a network event is received.
     *
     * @param event
     *            Network event.
     */
    void onNetworkEvent(NetworkEvent event);
}
