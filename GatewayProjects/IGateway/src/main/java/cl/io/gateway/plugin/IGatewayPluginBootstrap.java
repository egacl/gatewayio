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
package cl.io.gateway.plugin;

import cl.io.gateway.IGateway;

/**
 * Interface that allows to define a class for the initialization of a plugin to
 * make available in the gateway.
 *
 * @author egacl
 *
 */
public interface IGatewayPluginBootstrap {

    /**
     * This method allows you to receive an instance of the gateway so that the
     * plugin can perform initialization operations and interact with the platform.
     *
     * @param gateway
     *            gateway instance
     * @throws Exception
     *             if an error ocurrs
     */
    void initialize(IGateway gateway) throws Exception;

    /**
     * Method invoked to stop plugin operation.
     *
     * @throws Exception
     *             if an error ocurrs
     */
    void stop() throws Exception;

    /**
     * Method invoked to obtain an instance of a plugin to be used by a gateway
     * service.
     *
     * @param pluginType
     *            Plugin class type
     * @return plugin instance to be used by a service
     */
    <T> T getPluginInstance(Class<T> pluginType);
}
