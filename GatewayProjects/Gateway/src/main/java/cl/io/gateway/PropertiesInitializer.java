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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.exception.GatewayInitilizationException;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkConfiguration;
import cl.io.gateway.network.driver.IEventMessageCodec;
import cl.io.gateway.network.driver.INetworkDriver;
import cl.io.gateway.properties.XProperties;

public class PropertiesInitializer {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesInitializer.class);

    /*
     * Main properties
     */
    private static final String PROP_ID = "gateway.id";

    private static final String PROP_NETWORK_CONF_PREFIX = "gateway.network";

    private static final String PROP_PATH_PLUGINS = "gateway.path.plugins";

    private static final String PROP_PATH_SERVICES = "gateway.path.services";

    /*
     * Network properties
     */
    private static final String PROP_NET_ORIGIN = "network.source";

    private static final String PROP_NET_IP = "network.ip";

    private static final String PROP_NET_PORT = "network.port";

    private static final String PROP_NET_DRIVER = "network.driver";

    private static final String PROP_NET_CODEC = "network.codec";

    private static final String PROP_NET_IDDLE = "network.iddle.seconds";

    private static final String PROP_NET_MAX_TIMEPOUT = "network.max.timeout";

    private static final String PROP_NET_ACCEPT_CLIENTS = "network.accept.clients";

    private static final String PROP_NET_PATH = "network.path";

    private final XProperties properties;

    private final String gatewayId;

    private final GatewayResourcesLoader resourcesLoader;

    private final Map<String, NetworkConfiguration> networkConfigurationMap;

    public PropertiesInitializer(XProperties properties) throws Exception {
        this.properties = properties;
        this.gatewayId = this.properties.readMandatoryProperty(PROP_ID);
        this.resourcesLoader = new GatewayResourcesLoader();
        this.networkConfigurationMap = new HashMap<String, NetworkConfiguration>();
    }

    public void loadNetWorkConfiguration() throws Exception {
        String[] networkConfigProperties = this.properties.getSubsetList(PROP_NETWORK_CONF_PREFIX, '.');
        for (String networkConfFile : networkConfigProperties) {
            logger.info("Read network configuration: " + this.properties.readMandatoryProperty(networkConfFile));
            XProperties networkProperties = XProperties
                    .loadPropertiesFile(this.properties.readMandatoryProperty(networkConfFile));
            NetworkConfiguration networkConfiguration = this.getNetworkConfiguration(networkProperties);
            NetworkServiceSource origin = NetworkServiceSource
                    .valueOf(networkProperties.readMandatoryProperty(PROP_NET_ORIGIN));
            if (this.networkConfigurationMap.containsKey(origin.name())) {
                throw new GatewayInitilizationException(
                        "There are two network configuration files for " + origin + " origin");
            }
            this.networkConfigurationMap.put(origin.name(), networkConfiguration);
        }
    }

    public void loadPlugins() throws Exception {
        logger.info(
                "Load plugins classpath from directory: " + this.properties.readMandatoryProperty(PROP_PATH_PLUGINS));
        File pluginsDirectory = new File(this.properties.readMandatoryProperty(PROP_PATH_PLUGINS));
        for (File dir : pluginsDirectory.listFiles()) {
            if (dir.isFile() || dir.isHidden()) {
                continue;
            }
            logger.info("Loading '" + dir.getName() + "' plugin");
            resourcesLoader.addPluginClassLoader(dir.getName(), dir);
        }
    }

    public void loadServices() throws Exception {
        logger.info(
                "Load services classpath from directory: " + this.properties.readMandatoryProperty(PROP_PATH_SERVICES));
        File servicesDirectory = new File(this.properties.readMandatoryProperty(PROP_PATH_SERVICES));
        for (File dir : servicesDirectory.listFiles()) {
            if (dir.isFile() || dir.isHidden()) {
                continue;
            }
            logger.info("Loading '" + dir.getName() + "' service");
            resourcesLoader.addServiceClassLoader(dir.getName(), dir);
        }
    }

    private NetworkConfiguration getNetworkConfiguration(final XProperties properties) throws Exception {
        return new NetworkConfiguration(properties.getInteger(PROP_NET_PORT),
                properties.readMandatoryProperty(PROP_NET_PATH)).ip(properties.readMandatoryProperty(PROP_NET_IP))
                        .acceptClients(properties.getBoolean(PROP_NET_ACCEPT_CLIENTS, true))
                        .iddleTimeInSeconds(properties.getInteger(PROP_NET_IDDLE, 10))
                        .maxTimeOuts(properties.getInteger(PROP_NET_MAX_TIMEPOUT, 3))
                        .networkDriver(
                                this.loadClass(properties.readMandatoryProperty(PROP_NET_DRIVER), INetworkDriver.class))
                        .addCodec(this
                                .loadClass(properties.readMandatoryProperty(PROP_NET_CODEC), IEventMessageCodec.class)
                                .newInstance());
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> loadClass(String className, Class<T> type) throws Exception {
        return (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public XProperties getProperties() {
        return properties;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public GatewayResourcesLoader getResourcesLoader() {
        return resourcesLoader;
    }

    public Map<String, NetworkConfiguration> getNetworkConfigurationMap() {
        return networkConfigurationMap;
    }
}
