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
 * Annotation interface that allows the definition of a filter for a network
 * event.
 * 
 * @see IGatewayMessageFilter
 * @author egacl
 *
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GatewayMessageFilter {

    /**
     * Priority or order of execution of the filter. Lower value of this attribute
     * indicates that the filter will be executed before those with higher values.
     *
     * @return priority value
     */
    int value() default 1;

    /**
     * Identifier of the network event to be filtered.
     *
     * @return event identifier
     */
    String event();

    /**
     * Object type contained in the network message to be filtered.
     *
     * @return object class type
     */
    Class<?> messageType();
}
