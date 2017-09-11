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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.InternalService.MethodParameterType;
import cl.io.gateway.messaging.IGatewayMessageHandler;
import cl.io.gateway.service.IGatewayService;

public class InternalGatewayService extends AbstractGateway<IGatewayService> {

    private static final Logger logger = LoggerFactory.getLogger(InternalGatewayService.class);

    private final InternalService service;

    private final Map<String, InternalServiceHandler<?>> eventsHandlerMap;

    @SuppressWarnings("unchecked")
    public InternalGatewayService(Gateway gateway, InternalService service) throws Exception {
        super(gateway, service.getServiceId(), service.getGatewayServiceId(),
                (Class<IGatewayService>) service.getServiceClass());
        this.service = service;
        this.eventsHandlerMap = new ConcurrentHashMap<String, InternalServiceHandler<?>>();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init() throws Exception {
        logger.info("Initialiazing gateway service '" + this.service.getGatewayServiceId() + "' from '"
                + this.service.getServiceId() + "' context");
        // Add anotated message handlers
        for (Map.Entry<String, MethodParameterType> messageHandler : this.service.getEventMethodMap().entrySet()) {
            // Add listener for event
            this.getGateway()
                    .addMessageHandler(messageHandler.getKey(), this.createHandler(messageHandler.getKey(),
                            messageHandler.getValue().getParameterType(), messageHandler.getValue().getMethod()),
                            messageHandler.getValue().getOrigins());
        }
        this.getServiceInstance().initialize(this);
    }

    private <T> IGatewayMessageHandler<T> createHandler(final String event, final Class<T> messageType,
            final Method method) {
        final InternalServiceHandler<T> internalHandler = new InternalServiceHandler<T>(event, messageType, method);
        this.eventsHandlerMap.put(event, internalHandler);
        return internalHandler;
    }
}
