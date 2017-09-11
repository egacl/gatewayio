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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cl.io.gateway.messaging.NetworkServiceSource;

/**
 * Annotation interface that allows you to define a network authentication
 * service that will apply to all connections related to a network service
 * source and events related to the defined authentication protocol.
 *
 * @author egacl
 *
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationService {

    /**
     * Defines the network service source for which the authentication service will
     * be applied. This authentication service will apply to each of the incoming
     * connections of the network service independent of the applications or
     * services that are installed in the gateway.
     *
     * @return network service source value
     */
    NetworkServiceSource value();

    /**
     * It defines the identifier of the events involved in the authentication
     * workflow that has been defined. For these events, the system will not request
     * that the sending or receiving client is fully authenticated.
     * 
     * @return protocols events string array
     */
    String[] authProtocolEvents();
}
