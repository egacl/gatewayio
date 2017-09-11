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
package cl.io.gateway.auth;

import cl.io.gateway.messaging.IGatewayNetworkService;
import cl.io.gateway.network.handler.INetworkEventListener;
import cl.io.gateway.vo.GatewayClient;

/**
 * This interface represents a specialized network source to be used by the
 * authentication service. This allows the authentication service to be able to
 * communicate the authentication status of the connected channels and to access
 * lower level network events.
 *
 * @see GatewayClient
 * @see INetworkEventListener
 * @author egacl
 *
 */
public interface IAuthenticationGatewayNetworkService extends IGatewayNetworkService {

    /**
     * This method allows the authentication service to communicate the
     * authentication status of a channel.
     *
     * @param client
     *            channel or cliente connected to the gateway
     * @param status
     *            new authentication status
     */
    void clientAuthenticated(GatewayClient client, AuthenticationStatus status);

    /**
     * This method allows you to add a network connection status handler directly to
     * the network service.
     *
     * @param handler
     *            handler instance
     */
    void addNetworkEventHandler(INetworkEventListener handler);

    /**
     * This method allows you to remove a network connection status handler directly
     * to the network service.
     *
     * @param handler
     *            handler instance
     * @return true if the handler was removed
     */
    boolean removeNetworkEventHandler(INetworkEventListener handler);
}
