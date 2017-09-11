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

import cl.io.gateway.IGatewayClientSession;

/**
 * This interface allows you to add a handler or listener to subscribe to
 * notifications in the status changes of the clients or channels connected to
 * the gateway.
 *
 * @author egacl
 *
 */
public interface IAuthenticationStatusListener {

    /**
     * Method invoked when a state change exists in a client connected to the
     * gateway.
     * 
     * @param session
     *            client session
     * @param status
     *            new status
     */
    void onStatusChanged(IGatewayClientSession session, AuthenticationStatus status);
}
