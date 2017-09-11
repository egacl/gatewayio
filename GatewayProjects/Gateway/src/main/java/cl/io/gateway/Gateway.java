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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.exception.GatewayProcessException;
import cl.io.gateway.messaging.IGatewayMessageHandler;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;

class Gateway {

    private static final Logger logger = LoggerFactory.getLogger(Gateway.class);

    private static final Object INSTANCE_LOCK = new Object();

    private final Map<String, NetworkServiceManager> networkServiceManagerMap;

    private final EnvironmentReader environment;

    private final Map<String, InternalGatewayService> gatewayServicesMap;

    private static Gateway instance = null;

    public static Gateway createInstance(final EnvironmentReader environment) {
        synchronized (INSTANCE_LOCK) {
            if (instance == null) {
                logger.info("Create gateway instance");
                instance = new Gateway(environment);
            }
            return instance;
        }
    }

    public static Gateway getInstance() {
        synchronized (INSTANCE_LOCK) {
            return instance;
        }
    }

    private Gateway(final EnvironmentReader environment) {
        this.environment = environment;
        this.networkServiceManagerMap = new ConcurrentHashMap<String, NetworkServiceManager>();
        this.gatewayServicesMap = new ConcurrentHashMap<String, InternalGatewayService>();
    }

    public void load() throws Exception {
        // TODO Load plugins
        // Load authentication services
        for (InternalAuthenticationService auth : this.environment.getMessagingAuthServicesMap().values()) {
            NetworkServiceManager servManager = this.environment.createNetworkServiceManager(this, auth.getOrigin());
            this.networkServiceManagerMap.put(auth.getOrigin().name(), servManager);
            // Load filters
            for (Map.Entry<String, List<InternalMessageFilter<?>>> set : this.environment.getMessageFilterEventsMap()
                    .entrySet()) {
                for (InternalMessageFilter<?> filter : set.getValue()) {
                    InternalMessageFilter<?> newIns = new InternalMessageFilter<>(filter);
                    servManager.addMessageFilter(newIns);
                    newIns.init();
                }
            }
            servManager.init();
        }
        // Load services
        for (InternalService service : this.environment.getGatewayServicesMap().values()) {
            final InternalGatewayService gwService = new InternalGatewayService(this, service);
            this.gatewayServicesMap.put(service.getGatewayServiceId(), gwService);
            gwService.init();
        }
    }

    public void start() throws Exception {
        // Start network
        for (NetworkServiceManager net : this.networkServiceManagerMap.values()) {
            logger.info("Starting " + net.getOrigin() + " network service");
            net.start();
        }
    }

    public <T> void sendMessage(IGatewayClientSession client, NetworkMessage<T> message, NetworkServiceSource... origin)
            throws NetworkDriverException {
        NetworkServiceSource[] or = origin;
        if (origin == null) {
            or = NetworkServiceSource.values();
        }
        for (NetworkServiceSource o : or) {
            NetworkServiceManager net = this.networkServiceManagerMap.get(o.name());
            if (net == null) {
                continue;
            }
            net.sendNetworkMessage(client, message);
        }
    }

    public <T> void addMessageHandler(String event, IGatewayMessageHandler<T> handler) throws GatewayProcessException {
        this.addMessageHandler(event, handler, NetworkServiceSource.values());
    }

    public <T> void addMessageHandler(String event, IGatewayMessageHandler<T> handler, NetworkServiceSource... origin)
            throws GatewayProcessException {
        for (NetworkServiceSource o : origin) {
            NetworkServiceManager net = this.networkServiceManagerMap.get(o.name());
            if (net == null) {
                continue;
            }
            net.addMessageHandler(event, handler);
        }
    }

    public void removeMessageHandler(String event) throws GatewayProcessException {
        this.removeMessageHandler(event, NetworkServiceSource.values());
    }

    public void removeMessageHandler(String event, NetworkServiceSource... origin) throws GatewayProcessException {
        for (NetworkServiceSource o : origin) {
            NetworkServiceManager net = this.networkServiceManagerMap.get(o.name());
            if (net == null) {
                continue;
            }
            net.removeMessageHandler(event);
        }
    }
}
