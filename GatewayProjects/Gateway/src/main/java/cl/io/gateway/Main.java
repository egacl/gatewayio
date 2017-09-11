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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.properties.XProperties;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final Object THREAD_LOCK = new Object();

    public static void main(String[] args) throws Exception {
        logger.info("Initializing gateway.io!\n\n" + " _____       _                                 _       \n"
                + "|  __ \\     | |                               (_)      \n"
                + "| |  \\/ __ _| |_ _____      ____ _ _   _       _  ___  \n"
                + "| | __ / _` | __/ _ \\ \\ /\\ / / _` | | | |     | |/ _ \\ \n"
                + "| |_\\ \\ (_| | ||  __/\\ V  V / (_| | |_| |  _  | | (_) |\n"
                + " \\____/\\__,_|\\__\\___| \\_/\\_/ \\__,_|\\__, | (_) |_|\\___/ \n"
                + "                                    __/ |              \n"
                + "                                   |___/               \n\n\n");
        logger.info("\n\n\n\n############################################\nInitializing reading properties file: "
                + args[0]);
        // Get and read principal properties file
        PropertiesInitializer propertiesReader = new PropertiesInitializer(XProperties.loadPropertiesFile(args[0]));
        logger.info("\n\t==========================================================================================\n"
                + "\t\tGATEWAY_ID ====================> '" + propertiesReader.getGatewayId()
                + "' <==================== GATEWAY_ID\n"
                + "\t==========================================================================================");
        // Load networks configurations
        propertiesReader.loadNetWorkConfiguration();
        // Load plugins classpath
        propertiesReader.loadPlugins();
        // Load services classpath
        propertiesReader.loadServices();
        logger.info(
                "\n\n\n\n############################################\nInitializing reading resources and clases process");
        // Calling environment reader
        EnvironmentReader environmentReader = new EnvironmentReader(propertiesReader);
        // Network authentication initalization
        environmentReader.networkInitializer();
        // Plugins initialization
        environmentReader.pluginsInitializer();
        // Services initialization
        environmentReader.servicesAndHandlersInitializer();
        // Filters initialization
        environmentReader.filtersInitializer();
        logger.info("\n\n\n\n############################################\nInitializing loading resources process");
        // Create gateway instance
        final Gateway gateway = Gateway.createInstance(environmentReader);
        // Load envinronment
        gateway.load();
        logger.info("\n\n\n\n############################################\nNetwork starting process");
        gateway.start();
        // Block main thread
        synchronized (THREAD_LOCK) {
            try {
                THREAD_LOCK.wait();
            } catch (final InterruptedException e) {
            }
        }
    }
}
