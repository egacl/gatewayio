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

import cl.io.gateway.exception.GatewayProcessException;
import cl.io.gateway.messaging.GatewayMessageContext;
import cl.io.gateway.messaging.IGatewayMessageHandler;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;

public class AbstractGateway<I> implements IGateway {

    private final Gateway gateway;

    private final String serviceId;

    private final String gatewayServiceId;

    private final Map<String, InternalServiceHandler<?>> eventsHandlerMap;

    private final I serviceInstance;

    private final Class<I> instanceClass;

    public AbstractGateway(final Gateway gateway, final String serviceId, final String gatewayServiceId,
            Class<I> instanceClass) throws Exception {
        this.gateway = gateway;
        this.serviceId = serviceId;
        this.gatewayServiceId = gatewayServiceId;
        this.eventsHandlerMap = new ConcurrentHashMap<String, InternalServiceHandler<?>>();
        this.instanceClass = instanceClass;
        this.serviceInstance = this.instanceClass.newInstance();
    }

    @Override
    public <T> void addMessageHandler(String event, IGatewayMessageHandler<T> handler) throws GatewayProcessException {
        if (this.eventsHandlerMap.containsKey(event)) {
            throw new GatewayProcessException("Event '" + event + "' is already associated with another handler");
        }
        final InternalServiceHandler<T> internalHandler = new InternalServiceHandler<T>(event, handler);
        this.eventsHandlerMap.put(event, internalHandler);
        this.gateway.addMessageHandler(event, internalHandler);
    }

    @Override
    public void removeMessageHandler(String event) throws GatewayProcessException {
        final InternalServiceHandler<?> internalHandler = this.eventsHandlerMap.get(event);
        if (internalHandler.reflection) {
            throw new GatewayProcessException("Event '" + event + "' is associated with the method "
                    + internalHandler.method + ". It cannot be removed");
        }
        this.eventsHandlerMap.remove(event);
        this.gateway.removeMessageHandler(event);
    }

    @Override
    public <T> void sendMessage(IGatewayClientSession client, NetworkMessage<T> message, NetworkServiceSource... origin)
            throws NetworkDriverException {
        this.gateway.sendMessage(client, message, origin);
    }

    public Gateway getGateway() {
        return gateway;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getGatewayServiceId() {
        return gatewayServiceId;
    }

    public Map<String, InternalServiceHandler<?>> getEventsHandlerMap() {
        return eventsHandlerMap;
    }

    public I getServiceInstance() {
        return serviceInstance;
    }

    public Class<I> getInstanceClass() {
        return instanceClass;
    }

    class InternalServiceHandler<T> implements IGatewayMessageHandler<T> {

        final boolean reflection;

        final String event;

        final Class<T> messageType;

        final Method method;

        final IGatewayMessageHandler<T> handler;

        public InternalServiceHandler(final String event, final Class<T> messageType, final Method method) {
            this.reflection = true;
            this.event = event;
            this.messageType = messageType;
            this.method = method;
            this.handler = null;
        }

        public InternalServiceHandler(final String event, final IGatewayMessageHandler<T> handler) {
            this.reflection = false;
            this.event = event;
            this.handler = handler;
            this.method = null;
            this.messageType = null;
        }

        @Override
        public void onMessage(NetworkMessage<T> message, IGatewayClientSession clientSession) throws Exception {
            // Create new session with this gateway implementation
            GatewayClientSession newSession = new GatewayClientSession(AbstractGateway.this,
                    (GatewayClientSession) clientSession);
            if (this.reflection) {
                method.invoke(AbstractGateway.this.serviceInstance, new GatewayMessageContext<>(message, newSession));
            } else {
                this.handler.onMessage(message, newSession);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbstractGateway [gateway=");
        builder.append(gateway);
        builder.append(", serviceId=");
        builder.append(serviceId);
        builder.append(", gatewayServiceId=");
        builder.append(gatewayServiceId);
        builder.append(", eventsHandlerMap=");
        builder.append(eventsHandlerMap);
        builder.append(", serviceInstance=");
        builder.append(serviceInstance);
        builder.append(", instanceClass=");
        builder.append(instanceClass);
        builder.append("]");
        return builder.toString();
    }
}
