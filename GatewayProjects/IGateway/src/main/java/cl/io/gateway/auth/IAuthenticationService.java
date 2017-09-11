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

import cl.io.gateway.IGateway;

/**
 * This interface represents an authentication service for a gateway network
 * source.
 *
 * @author egacl
 *
 */
public interface IAuthenticationService {

    /**
     * Method called to initialize the authentication service.
     *
     * @param gateway
     *            gateway instance
     * @param netService
     *            authentication network service interface
     * @throws Exception
     *             if an error ocurrs
     */
    void initialize(IGateway gateway, IAuthenticationGatewayNetworkService netService) throws Exception;
}
