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

/**
 * Enumeration that represents the possible states of a channel at the moment of
 * connecting to a gateway.
 *
 * @see AuthenticationService
 * @see IAuthenticationService
 * @author egacl
 *
 */
public enum AuthenticationStatus {
    /**
     * Indicates the channel is correctly authenticated at the gateway and is able
     * to send and receive network events.
     */
    LOGGED_IN,
    /**
     * Indicates that the channel is not properly authenticated at the gateway so
     * you can not send or receive network events.
     */
    LOGGED_OUT,
    /**
     * Indicates that a channel is in the process of being authenticated in the
     * gateway, so it is not yet enabled to send or receive network events except
     * those defined as "protocol events" in the authentication service.
     */
    PROCESS_LOGGING;
}
