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
import cl.io.gateway.network.NetworkConnection;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.NetworkService;
import cl.io.gateway.network.codec.GSonMessageCodec;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.websocketdriver.WebSocketDriver;

/**
 * This class shows an example of how to interact with the network service. The
 * network service consists of a series of libraries that allow abstracting of
 * the network protocol used by exposing the developer to a paradigm of sending
 * and receiving objects.
 *
 * In this example of 'client' 4 different classes are used:
 *
 * "NetworkConfiguration": allows to define the network characteristics, such
 * as: port, network driver, message codec, etc.
 *
 * "INetworkService": Performs I / O operations using java and listener objects.
 *
 * "NetworkMessage": Class that contains inside any pojo to be sent or received
 * through the network.
 *
 * "NetworkConnection": It allows to define the data of connection to some
 * server.
 *
 * @author egacl
 */
public class MainClient {

    private static final Object THREAD_LOCK = new Object();

    private static final String TEST_EVENT = "testevent";

    public static void main(String[] args) throws Exception {
        // Initialice just client network configuration and set messages codec and
        // network driver
        NetworkConfiguration netConf = new NetworkConfiguration().addCodec(new GSonMessageCodec())
                .networkDriver(WebSocketDriver.class);
        // Initialice network service
        final INetworkService networkService = new NetworkService(netConf);
        // Add network connection event handler
        networkService.addNetworkEventListener((NetworkEvent event) -> {
            if (event.getEventType() == NetworkEventType.ACTIVE) {
                // When connection is active then send a message
                try {
                    System.out.println("Sending message to: " + event.getChannelId());
                    // Send a network message associated to "test" event
                    networkService.send(event.getChannelId(),
                            new NetworkMessage<>(TEST_EVENT, new TestMessage("test message")));
                } catch (NetworkDriverException err) {
                    err.printStackTrace();
                }
            }
        });
        // Add network message handler to process "testevent" event
        networkService.addMessageHandler(TEST_EVENT, (NetworkMessage<TestMessage> message) -> {
            // Process "testevent" network event messsage
            System.out.println("Receive response number '" + message.getChannelMessageSequence() + "' from '"
                    + message.getOriginChannelId() + "' server: " + message);
            // message echo
            networkService.send(message.getOriginChannelId(), message);
        });
        // Try to connect to any websocket server
        networkService.start().connectTo(new NetworkConnection("myServerId", "ws://localhost:7010/ws"));
        // Block main thread
        synchronized (THREAD_LOCK) {
            try {
                THREAD_LOCK.wait();
            } catch (final InterruptedException e) {
            }
        }
    }
}
