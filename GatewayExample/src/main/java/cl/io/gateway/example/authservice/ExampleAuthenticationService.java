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
package cl.io.gateway.example.authservice;

import cl.io.gateway.IGateway;
import cl.io.gateway.auth.AuthenticationService;
import cl.io.gateway.auth.AuthenticationStatus;
import cl.io.gateway.auth.IAuthenticationGatewayNetworkService;
import cl.io.gateway.auth.IAuthenticationService;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.handler.INetworkEventListener;
import cl.io.gateway.vo.GatewayClient;

/**
 * Sample class of authentication service. The @AuthenticationService annotation
 * and the IAuthenticationService interface must be used for the framework to
 * detect and initialize.
 *
 * This example service does not perform any authentication type, it only
 * listens to network events and when a client connects it marks it as
 * 'authenticated' and when it is disconnected it marks it as 'disconnected'.
 *
 * @author egacl
 *
 */
@AuthenticationService(authProtocolEvents = { "LOGIN, LOGOUT" }, value = NetworkServiceSource.ADMIN)
public class ExampleAuthenticationService implements IAuthenticationService {

    @Override
    public void initialize(final IGateway gateway, final IAuthenticationGatewayNetworkService netService)
            throws Exception {
        System.out.println("Hello! I'm an authentication service!");
        // a listener is added to capture client connection and disconnection network
        // events
        netService.addNetworkEventListener(new INetworkEventListener() {

            @Override
            public void onEvent(NetworkEvent event) {
                if (event.getEventType() == NetworkEventType.ACTIVE) {
                    // when the network driver notifies a connected client, it is marked as
                    // 'authenticated' at the gateway
                    netService.clientAuthenticated(new GatewayClient(event.getChannelId()),
                            AuthenticationStatus.LOGGED_IN);
                } else if (event.getEventType() == NetworkEventType.INACTIVE) {
                    // when the network driver notifies a client it is disconnected, then it is
                    // marked as 'disconnected' at the gateway
                    netService.clientAuthenticated(new GatewayClient(event.getChannelId()),
                            AuthenticationStatus.LOGGED_OUT);
                }
            }
        });
    }
}
