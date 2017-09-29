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

import cl.io.gateway.plugin.IGatewayPluginBootstrap;

public class InternalPlugin extends InternalElement<IGatewayPluginBootstrap> {

    private final String description;

    private final Class<?> pluginType;

    public InternalPlugin(String description, Class<?> pluginType, String contextId,
            Class<IGatewayPluginBootstrap> serviceClass, String gatewayId) {
        super(contextId, serviceClass, gatewayId);
        this.description = description;
        this.pluginType = pluginType;
    }

    public String getDescription() {
        return description;
    }

    public Class<?> getPluginType() {
        return pluginType;
    }
}
