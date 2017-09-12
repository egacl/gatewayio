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
package cl.io.gateway.example.networkservice;

import cl.io.gateway.network.INetworkService;
import cl.io.gateway.network.NetworkConfiguration;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.NetworkService;
import cl.io.gateway.network.codec.GSonMessageCodec;
import cl.io.gateway.websocketdriver.WebSocketDriver;

/**
 * This class shows an example of how to interact with the network service. The
 * network service consists of a series of libraries that allow abstracting of
 * the network protocol used by exposing the developer to a paradigm of sending
 * and receiving objects.
 *
 * In this example of 'server' 3 different classes are used:
 *
 * "NetworkConfiguration": allows to define the network characteristics, such
 * as: port, network driver, message codec, etc.
 *
 * "INetworkService": Performs I / O operations using java and listener objects.
 *
 * "NetworkMessage": Class that contains inside any pojo to be sent or received
 * through the network.
 *
 * @author egacl
 */
public class MainServer {

    private static final Object THREAD_LOCK = new Object();

    private static final String TEST_EVENT = "testevent";

    public static void main(String[] args) throws Exception {
        // Initialice server network configuration and set messages codec and network
        // driver, listening on 7010 port
        NetworkConfiguration netConf = new NetworkConfiguration(7010).path("/ws").addCodec(new GSonMessageCodec())
                .networkDriver(WebSocketDriver.class);
        // Network service initialization
        final INetworkService networkService = new NetworkService(netConf);
        networkService.addNetworkEventListener((NetworkEvent event) -> {
            System.out.println("Evento de red: " + event);
        });
        // Add network message handler to process "testevent" event from any client
        networkService.addMessageHandler(TEST_EVENT, (NetworkMessage<TestMessage> clientMessage) -> {
            // Process "testevent" network event messsage received from any client
            System.out.println("Receive clientMessage number '" + clientMessage.getChannelMessageSequence() + "' from '"
                    + clientMessage.getOriginChannelId() + "' client: " + clientMessage + ", events counter: "
                    + clientMessage.getEventMessageSequence());
            // message echo
            networkService.send(clientMessage.getOriginChannelId(), clientMessage);
        });
        // Start network messaging service
        networkService.start();
        // Block main thread
        synchronized (THREAD_LOCK) {
            try {
                THREAD_LOCK.wait();
            } catch (final InterruptedException e) {
            }
        }
    }
}
