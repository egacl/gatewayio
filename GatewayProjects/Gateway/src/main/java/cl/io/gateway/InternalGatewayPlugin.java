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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.classloader.GatewayClassLoader;
import cl.io.gateway.plugin.IGatewayPluginBootstrap;
import cl.io.gateway.properties.XProperties;

public class InternalGatewayPlugin extends AbstractGateway<IGatewayPluginBootstrap, InternalPlugin> {

    private static final Logger logger = LoggerFactory.getLogger(IGatewayPluginBootstrap.class);

    public InternalGatewayPlugin(Gateway gateway, InternalPlugin plugin) throws Exception {
        super(gateway, plugin);
    }

    @Override
    public void init() throws Exception {
        logger.info("Initializing plugin '" + this.getElement().getInstanceableClass() + "' from '"
                + this.getContextId() + "' context");
        super.init();
        this.getElementInstance().initialize(this);
    }

    @Override
    public XProperties getProperties(String propertyFileName) throws IOException {
        GatewayClassLoader ccl = this.getGateway().getEnvironmentReader().getPropertiesInicializer().getResourcesLoader()
                .getPluginClassLoader(this.getElement().getContextId());
        return XProperties.loadFromFileOrClasspath(ccl.getPropsPath(), propertyFileName,
                this.getElement().getInstanceableClass());
    }
}
