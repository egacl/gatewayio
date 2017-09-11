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
package cl.io.gateway.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation interface that allows the definition of a handler for a network
 * message.
 *
 * @author egacl
 *
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface GatewayMessageHandler {

    /**
     * Identifier of the network event to be processed.
     *
     * @return event identifier
     */
    String value();

    /**
     * Defines the source of the network service by which the expected event will be
     * received. If this attribute is not defined then all the available network
     * services are assumed as sources.
     *
     * @return network sources for this event
     */
    NetworkServiceSource[] sources() default {};
}
