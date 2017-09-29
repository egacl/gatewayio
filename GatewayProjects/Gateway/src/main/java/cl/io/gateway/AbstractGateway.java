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
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.InternalElement.MethodParameterType;
import cl.io.gateway.InternalElement.PluginField;
import cl.io.gateway.classloader.GatewayClassLoader;
import cl.io.gateway.exception.GatewayProcessException;
import cl.io.gateway.messaging.GatewayMessageContext;
import cl.io.gateway.messaging.IGatewayMessageHandler;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.properties.XProperties;

public abstract class AbstractGateway<I, E extends InternalElement<I>> implements IGateway {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGateway.class);

    private final Gateway gateway;

    private final Map<String, InternalMessageHandler<?>> eventsHandlerMap;

    private final I elementInstance;

    private final InternalElement<I> element;

    public AbstractGateway(final Gateway gateway, final InternalElement<I> element) throws Exception {
        this.gateway = gateway;
        this.element = element;
        this.eventsHandlerMap = new ConcurrentHashMap<String, InternalMessageHandler<?>>();
        this.elementInstance = this.element.getInstanceableClass().newInstance();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init() throws Exception {
        if (!this.element.getEventMethodMap().isEmpty()) {
            // Add anotated message handlers
            for (Map.Entry<String, MethodParameterType> messageHandler : this.element.getEventMethodMap().entrySet()) {
                // Add listener for event
                this.gateway.addMessageHandler(
                        messageHandler.getKey(), this.createHandler(messageHandler.getKey(),
                                messageHandler.getValue().getParameterType(), messageHandler.getValue().getMethod()),
                        messageHandler.getValue().getOrigins());
            }
        }
        if (!this.element.getPluginFieldsList().isEmpty()) {
            for (PluginField plugin : this.element.getPluginFieldsList()) {
                plugin.getField().setAccessible(true);
                // Plugin is requested to instantiate the object to inject into the field
                plugin.getField().set(this.elementInstance,
                        this.getPlugin(plugin.getPluginId(), plugin.getPluginFieldType()));
            }
        }
    }

    @Override
    public <T> T getPlugin(String pluginId, Class<T> pluginType) {
        InternalGatewayPlugin gwPlugin = this.gateway.getPlugin(pluginId);
        if (gwPlugin != null) {
            return gwPlugin.getElementInstance().getPluginInstance(pluginType);
        }
        logger.warn("'" + element.getGatewayId() + "' trying to invoke '" + pluginId + "' but not exists");
        return null;
    }

    @Override
    public <T> void addMessageHandler(String event, IGatewayMessageHandler<T> handler) throws GatewayProcessException {
        if (this.eventsHandlerMap.containsKey(event)) {
            throw new GatewayProcessException("Event '" + event + "' is already associated with another handler");
        }
        final InternalMessageHandler<T> internalHandler = new InternalMessageHandler<T>(event, handler);
        this.eventsHandlerMap.put(event, internalHandler);
        this.gateway.addMessageHandler(event, internalHandler);
    }

    @Override
    public void removeMessageHandler(String event) throws GatewayProcessException {
        final InternalMessageHandler<?> internalHandler = this.eventsHandlerMap.get(event);
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

    @Override
    public XProperties getProperties(String propertyFileName) throws IOException {
        GatewayClassLoader ccl = this.gateway.getEnvironmentReader().getPropertiesInicializer().getResourcesLoader()
                .getServiceContextClassLoader(this.element.getContextId());
        return XProperties.loadFromFileOrClasspath(ccl.getPropsPath(), propertyFileName,
                this.element.getInstanceableClass());
    }

    <T> IGatewayMessageHandler<T> createHandler(final String event, final Class<T> messageType, final Method method) {
        final InternalMessageHandler<T> internalHandler = new InternalMessageHandler<T>(event, messageType, method);
        this.eventsHandlerMap.put(event, internalHandler);
        return internalHandler;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public String getContextId() {
        return this.element.getContextId();
    }

    public String getGatewayId() {
        return this.element.getGatewayId();
    }

    public Map<String, InternalMessageHandler<?>> getEventsHandlerMap() {
        return eventsHandlerMap;
    }

    public I getElementInstance() {
        return elementInstance;
    }

    public Class<I> getElementInstanceClass() {
        return this.element.getInstanceableClass();
    }

    public InternalElement<I> getElement() {
        return this.element;
    }

    class InternalMessageHandler<T> implements IGatewayMessageHandler<T> {

        final boolean reflection;

        final String event;

        final Class<T> messageType;

        final Method method;

        final IGatewayMessageHandler<T> handler;

        public InternalMessageHandler(final String event, final Class<T> messageType, final Method method) {
            this.reflection = true;
            this.event = event;
            this.messageType = messageType;
            this.method = method;
            this.handler = null;
        }

        public InternalMessageHandler(final String event, final IGatewayMessageHandler<T> handler) {
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
                method.invoke(AbstractGateway.this.elementInstance, new GatewayMessageContext<>(message, newSession));
            } else {
                this.handler.onMessage(message, newSession);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("InternalMessageHandler [reflection=");
            builder.append(reflection);
            builder.append(", event=");
            builder.append(event);
            builder.append(", messageType=");
            builder.append(messageType);
            builder.append(", method=");
            builder.append(method);
            builder.append(", handler=");
            builder.append(handler);
            builder.append("]");
            return builder.toString();
        }
    }
}
