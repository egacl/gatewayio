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

import cl.io.gateway.auth.AuthenticationStatus;
import cl.io.gateway.auth.IAuthenticationGatewayNetworkService;
import cl.io.gateway.auth.IAuthenticationService;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.handler.INetworkEventListener;
import cl.io.gateway.vo.GatewayClient;

public class DefaultAuthenticationService implements IAuthenticationService {

    @Override
    public void initialize(final IGateway gateway, final IAuthenticationGatewayNetworkService netService)
            throws Exception {
        netService.addNetworkEventHandler(new INetworkEventListener() {

            @Override
            public void onEvent(NetworkEvent event) {
                if (event.getEventType() == NetworkEventType.ACTIVE) {
                    netService.clientAuthenticated(new GatewayClient(event.getChannelId()),
                            AuthenticationStatus.LOGGED_IN);
                } else if (event.getEventType() == NetworkEventType.INACTIVE) {
                    netService.clientAuthenticated(new GatewayClient(event.getChannelId()),
                            AuthenticationStatus.LOGGED_OUT);
                }
            }
        });
    }
}
