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
package cl.io.gateway.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cl.io.gateway.messaging.GatewayMessageHandler;

/**
 * Annotation interface representing a gateway service. A service is a high
 * level entity, which has a unique identifier in the gateway, where the
 * programmer can develop business logic using different events.
 *
 * @see IGatewayService
 * @see GatewayMessageHandler
 * @author egacl
 *
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GatewayService {

    /**
     * Unique identifier of the service.
     *
     * @return service id
     */
    String value();
}
