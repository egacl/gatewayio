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
package cl.io.gateway.plugin.hikaricp;

import java.io.File;
import java.util.Properties;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.JavassistProxyFactory;

import cl.io.gateway.IGateway;
import cl.io.gateway.classloader.GatewayClassLoader;
import cl.io.gateway.plugin.GatewayPluginDefinition;
import cl.io.gateway.plugin.IGatewayPluginBootstrap;
import cl.io.gateway.plugin.hikaricp.DriverManager.Driver;
import cl.io.gateway.properties.XProperties;

@GatewayPluginDefinition(
        value = "GatewayHikariCP",
        description = "Plugin that allows connection pool management for all gateway services",
        pluginType = IHikariDataSourceManager.class)
public class HikariConnectionPoolPlugin implements IGatewayPluginBootstrap {

    private final HikariDataSourceManager dsManager;

    private IGateway gateway;

    private DriverManager driverManager;

    public HikariConnectionPoolPlugin() {
        this.dsManager = new HikariDataSourceManager();
    }

    @Override
    public void initialize(IGateway gateway) throws Exception {
        this.gateway = gateway;
        this.driverManager = new DriverManager();
        // Get plugins class loader
        final GatewayClassLoader ccl = (GatewayClassLoader) Thread.currentThread().getContextClassLoader();
        // Read properties with availables drivers
        XProperties driversProperties = this.gateway.getProperties("drivers.properties");
        for (String driverProp : driversProperties.getSubsetList("hikari.driver", '.')) {
            Driver driverData = new Driver(driverProp, driversProperties.readMandatoryProperty(driverProp + "name"),
                    ccl.getMainPath() + File.separator + driversProperties.readMandatoryProperty(driverProp + "lib"));
            this.driverManager.addDriver(driverProp + "lib", driverData);
        }
        // Read properties with configurated datasources
        XProperties dataSourcessProperties = this.gateway.getProperties("datasources.properties");
        for (String datasourcesNameId : dataSourcessProperties.getStringArray("datasources")) {
            Properties hikariProps = this.toHikariProperties(datasourcesNameId,
                    dataSourcessProperties.filterProperties(datasourcesNameId));
            JavassistProxyFactory.main(new String[] {});
            XHikariConfig xconfig = new XHikariConfig(datasourcesNameId, hikariProps);
            // Create datasources connectionpool
            dsManager.addConfig(datasourcesNameId, xconfig, new HikariDataSource(xconfig.getConfig()));
        }
    }

    @Override
    public <T> T getPluginInstance(Class<T> pluginType) {
        return pluginType.cast(this.dsManager);
    }

    @Override
    public void stop() throws Exception {
        // TODO not implemented yet
    }

    /**
     * Allows you to transform the configuration of a datasource to the format that
     * can interpret hikari.
     *
     * @param datasourcesNameId
     *            unique datasource identifier
     * @param dsProps
     *            datasources in gateway plugin format
     * @return properties in hikari format
     */
    private Properties toHikariProperties(String datasourcesNameId, XProperties dsProps) {
        Properties props = new Properties();
        for (String dsProp : dsProps.getKeysArray()) {
            if (dsProp.contains("internalDriver")) {
                continue;
            }
            String hikariProp = dsProp.replaceAll(datasourcesNameId + ".", "");
            props.put(hikariProp, dsProps.getProperty(dsProp));
        }
        return props;
    }
}
