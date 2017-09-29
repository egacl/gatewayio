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
package cl.io.gateway.example.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import cl.io.gateway.IGateway;
import cl.io.gateway.plugin.GatewayPlugin;
import cl.io.gateway.plugin.hikaricp.IHikariDataSourceManager;
import cl.io.gateway.service.GatewayService;
import cl.io.gateway.service.IGatewayService;

/**
 * Example class that lets you see the use of the "GatewayPlugin" annotation
 * (and no annotation) for the invocation of a gateway plugin.
 *
 * @author egacl
 *
 */
@GatewayService("PluginInvocation")
public class ServiceWithPluginInvocation implements IGatewayService {

    /**
     * Gateway instance
     */
    private IGateway gateway;

    /**
     * Plugin invocation
     */
    @GatewayPlugin("GatewayHikariCP")
    private IHikariDataSourceManager dsManager;

    @Override
    public void initialize(IGateway gateway) {
        System.out.println("Hello world! I can invoque a connection pool plugin!");
        this.gateway = gateway;
        // Plugin test
        try (Connection c = dsManager.getDataSource("jdbc/exampleDS").getConnection();
                Statement s = c.createStatement();
                ResultSet r = s.executeQuery("SELECT SYSDATE FROM DUAL")) {
            // Database test query
            System.out.println(r.next());
            System.out.println(r.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Another way to invoke plugin
        IHikariDataSourceManager dsManager2 = this.gateway.getPlugin("GatewayHikariCP", IHikariDataSourceManager.class);
        System.out.println(dsManager2);
        // Plugin test
        try (Connection c = dsManager2.getDataSource("jdbc/exampleDS").getConnection();
                Statement s = c.createStatement();
                ResultSet r = s.executeQuery("SELECT SYSDATE FROM DUAL")) {
            // Database test query
            System.out.println(r.next());
            System.out.println(r.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
