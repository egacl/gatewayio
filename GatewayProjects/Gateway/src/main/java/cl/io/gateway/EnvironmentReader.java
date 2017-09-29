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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.InternalElement.MethodParameterType;
import cl.io.gateway.InternalElement.PluginField;
import cl.io.gateway.auth.AuthenticationService;
import cl.io.gateway.auth.IAuthenticationService;
import cl.io.gateway.exception.GatewayInitilizationException;
import cl.io.gateway.messaging.GatewayMessageContext;
import cl.io.gateway.messaging.GatewayMessageFilter;
import cl.io.gateway.messaging.GatewayMessageHandler;
import cl.io.gateway.messaging.IGatewayMessageFilter;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.plugin.GatewayPlugin;
import cl.io.gateway.plugin.GatewayPluginDefinition;
import cl.io.gateway.plugin.IGatewayPluginBootstrap;
import cl.io.gateway.service.GatewayService;
import cl.io.gateway.service.IGatewayService;

@SuppressWarnings("unchecked")
public class EnvironmentReader {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentReader.class);

    private final PropertiesInitializer propertiesInicializer;

    private final Map<String, InternalAuthenticationService> messagingAuthServicesMap;

    private final Map<String, InternalService> gatewayServicesMap;

    private final Map<String, InternalPlugin> gatewayPluginsMap;

    private final Map<String, List<InternalMessageFilter<?>>> messageFilterEventsMap;

    EnvironmentReader(PropertiesInitializer propertiesInicializer) {
        this.propertiesInicializer = propertiesInicializer;
        this.messagingAuthServicesMap = new HashMap<String, InternalAuthenticationService>();
        this.gatewayServicesMap = new HashMap<String, InternalService>();
        this.gatewayPluginsMap = new HashMap<String, InternalPlugin>();
        this.messageFilterEventsMap = new HashMap<String, List<InternalMessageFilter<?>>>();
    }

    public NetworkServiceManager createNetworkServiceManager(Gateway gateway, NetworkServiceSource origin)
            throws Exception {
        InternalAuthenticationService authServ = this.messagingAuthServicesMap.get(origin.name());
        return new NetworkServiceManager(gateway, propertiesInicializer.getNetworkConfigurationMap().get(origin.name()),
                new InternalGatewayAuthenticationService(authServ));
    }

    public void pluginsInitializer() throws Exception {
        for (String pluginId : this.propertiesInicializer.getResourcesLoader().getPluginsId()) {
            this.getGatewayPluginAndHandlers(pluginId);
        }
    }

    public void filtersInitializer() throws Exception {
        for (String serviceId : this.propertiesInicializer.getResourcesLoader().getContextsId()) {
            this.getGatewayFilters(serviceId);
        }
    }

    public void servicesAndHandlersInitializer() throws Exception {
        for (String serviceId : this.propertiesInicializer.getResourcesLoader().getContextsId()) {
            this.getGatewayServicesAndHandlers(serviceId);
        }
    }

    public void networkInitializer() throws Exception {
        // Find authentication services for all networks origins
        for (String serviceId : this.propertiesInicializer.getResourcesLoader().getContextsId()) {
            this.getAuthenticationService(serviceId);
        }
        // Validation for default authentication service implementation
        for (NetworkServiceSource origin : NetworkServiceSource.values()) {
            if (!this.messagingAuthServicesMap.containsKey(origin.name())) {
                // add default authenticacion
                logger.info("Network service for '" + origin
                        + "' origin implements default authentication service (no authentication)");
                this.messagingAuthServicesMap.put(origin.name(), new InternalAuthenticationService(
                        new HashSet<String>(), origin, "", DefaultAuthenticationService.class, ""));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void getGatewayFilters(String contextId) throws Exception {
        CustomClassLoader ccl = this.propertiesInicializer.getResourcesLoader().getServiceContextClassLoader(contextId);
        // Search gateways services
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("/", ClasspathHelper.contextClassLoader(),
                        ClasspathHelper.staticClassLoader()))
                .addUrls(ccl.getChildURL()).addClassLoader(ccl).addScanners(new MethodAnnotationsScanner()));
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(GatewayMessageFilter.class);
        for (Class<?> clas : annotated) {
            if (IGatewayMessageFilter.class.isAssignableFrom(clas)) {
                GatewayMessageFilter filterAttribs = clas.getAnnotation(GatewayMessageFilter.class);
                if (filterAttribs.event() == null || filterAttribs.event().isEmpty()) {
                    throw new GatewayInitilizationException("Missing value 'event' into " + GatewayMessageFilter.class
                            + " tag for " + clas.getName() + " gateway message filter");
                }
                // Get type of message by IGatewayMessageFilter
                Class<?> messageType = filterAttribs.messageType();
                if (messageType == null) {
                    throw new GatewayInitilizationException("Missing value 'messageType' into "
                            + GatewayMessageFilter.class + " tag for " + clas.getName() + " gateway message filter");
                }
                // Get filters list
                List<InternalMessageFilter<?>> internalFilterList = this.messageFilterEventsMap
                        .get(filterAttribs.event());
                if (internalFilterList == null) {
                    internalFilterList = new ArrayList<InternalMessageFilter<?>>();
                    this.messageFilterEventsMap.put(filterAttribs.event(), internalFilterList);
                }
                logger.info("Reading message filter handler for event '" + filterAttribs.event() + "' in class '" + clas
                        + "'");
                internalFilterList.add(new InternalMessageFilter(filterAttribs.value(), filterAttribs.event(), clas,
                        messageType, contextId));
            } else {
                throw new GatewayInitilizationException(
                        "'" + clas + "' must implements " + IGatewayService.class + " interface");
            }
        }
    }

    private void getGatewayPluginAndHandlers(String pluginId) throws Exception {
        CustomClassLoader ccl = this.propertiesInicializer.getResourcesLoader().getPluginClassLoader(pluginId);
        // Search gateway plugin
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("/", ClasspathHelper.contextClassLoader(),
                        ClasspathHelper.staticClassLoader()))
                .addUrls(ccl.getChildURL()).addClassLoader(ccl).addScanners(new MethodAnnotationsScanner()));
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(GatewayPluginDefinition.class);
        for (Class<?> clas : annotated) {
            if (IGatewayPluginBootstrap.class.isAssignableFrom(clas)) {
                GatewayPluginDefinition plugAttribs = clas.getAnnotation(GatewayPluginDefinition.class);
                if (plugAttribs.value() == null || plugAttribs.value().isEmpty()) {
                    throw new GatewayInitilizationException("Missing value 'GatewayPluginId' (value) into "
                            + GatewayService.class + " tag for " + clas.getName() + " gateway plugin");
                }
                if (this.gatewayPluginsMap.containsKey(plugAttribs.value())) {
                    throw new GatewayInitilizationException("There are another gateway plugin with ID '"
                            + plugAttribs.value() + "' check " + clas + " and "
                            + this.gatewayServicesMap.get(plugAttribs.value()).getInstanceableClass());
                }
                if (plugAttribs.description() == null || plugAttribs.description().isEmpty()) {
                    throw new GatewayInitilizationException("Missing value 'description' into " + GatewayService.class
                            + " tag for " + clas.getName() + " gateway plugin");
                }
                if (plugAttribs.pluginType() == null) {
                    throw new GatewayInitilizationException("Missing value 'pluginType' into " + GatewayService.class
                            + " tag for " + clas.getName() + " gateway plugin");
                }
                logger.info("Reading '" + plugAttribs.value() + "' represented by class " + clas);
                final InternalPlugin plugin = new InternalPlugin(plugAttribs.description(), plugAttribs.pluginType(),
                        pluginId, (Class<IGatewayPluginBootstrap>) clas, plugAttribs.value());
                this.gatewayPluginsMap.put(plugAttribs.value(), plugin);
                // Search gateway message handlers
                this.searchForMessagesHandlers(clas, plugin, ccl);
            }
        }
    }

    private void getGatewayServicesAndHandlers(String contextId) throws Exception {
        CustomClassLoader ccl = this.propertiesInicializer.getResourcesLoader().getServiceContextClassLoader(contextId);
        // Search gateways services
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("/", ClasspathHelper.contextClassLoader(),
                        ClasspathHelper.staticClassLoader()))
                .addUrls(ccl.getChildURL()).addClassLoader(ccl).addScanners(new MethodAnnotationsScanner()));
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(GatewayService.class);
        for (Class<?> clas : annotated) {
            if (IGatewayService.class.isAssignableFrom(clas)) {
                GatewayService servAttibs = clas.getAnnotation(GatewayService.class);
                if (servAttibs.value() == null || servAttibs.value().isEmpty()) {
                    throw new GatewayInitilizationException("Missing value 'GatewayServiceId' (value) into "
                            + GatewayService.class + " tag for " + clas.getName() + " gateway service");
                }
                if (this.gatewayServicesMap.containsKey(servAttibs.value())) {
                    throw new GatewayInitilizationException(
                            "There are another gateway service with ID '" + servAttibs.value() + "' check " + clas
                                    + " and " + this.gatewayServicesMap.get(servAttibs.value()).getInstanceableClass());
                }
                logger.info("Reading '" + servAttibs.value() + "' represented by class " + clas);
                final InternalService service = new InternalService(contextId, (Class<IGatewayService>) clas,
                        servAttibs.value());
                this.gatewayServicesMap.put(servAttibs.value(), service);
                // Search gateway message handlers
                this.searchForMessagesHandlers(clas, service, ccl);
                // Search for gateway plugin injection
                this.searchPluginsInvocation(clas, service, ccl);
            } else {
                throw new GatewayInitilizationException(
                        "'" + clas + "' must implements " + IGatewayService.class + " interface");
            }
        }
    }

    private void getAuthenticationService(String contextId) throws Exception {
        CustomClassLoader ccl = this.propertiesInicializer.getResourcesLoader().getServiceContextClassLoader(contextId);
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("/", ClasspathHelper.contextClassLoader(),
                        ClasspathHelper.staticClassLoader())).addUrls(ccl.getChildURL()).addClassLoader(ccl));
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AuthenticationService.class);
        for (Class<?> clas : annotated) {
            if (IAuthenticationService.class.isAssignableFrom(clas)) {
                AuthenticationService authAttibs = clas.getAnnotation(AuthenticationService.class);
                // tag validations for authentication services
                NetworkServiceSource origin = authAttibs.value();
                if (origin == null) {
                    throw new GatewayInitilizationException("Missing value 'AuthenticationOrigin' into "
                            + AuthenticationService.class + " tag for " + clas.getName() + " authentication service");
                }
                if (this.messagingAuthServicesMap.containsKey(origin.name())) {
                    throw new GatewayInitilizationException(
                            "There are two authentication services for origin '" + origin + "' check " + clas + " and "
                                    + this.messagingAuthServicesMap.get(origin.name()).getInstanceableClass());
                }
                Set<String> authProtocolsEvents = new HashSet<String>();
                if (authAttibs.authProtocolEvents() != null) {
                    authProtocolsEvents.addAll(Arrays.asList(authAttibs.authProtocolEvents()));
                }
                InternalAuthenticationService auth = new InternalAuthenticationService(authProtocolsEvents, origin,
                        contextId, (Class<IAuthenticationService>) clas, contextId);
                // Search gateway message handlers
                this.searchForMessagesHandlers(clas, auth, ccl);
                // Search for gateway plugin injection
                this.searchPluginsInvocation(clas, auth, ccl);
                // Add authentication service
                logger.info("Reading network service for '" + origin + "' origin implements '" + clas
                        + "' authentication service for these protocols events: "
                        + Arrays.toString(authProtocolsEvents.toArray(new String[0])));
                this.messagingAuthServicesMap.put(origin.name(), auth);
            } else {
                throw new GatewayInitilizationException(
                        "'" + clas + "' must implements " + IAuthenticationService.class + " interface");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void searchForMessagesHandlers(final Class<?> clas, final InternalElement<?> element,
            final CustomClassLoader ccl) throws Exception {
        // Search gateway message handlers
        for (Method method : clas.getMethods()) {
            GatewayMessageHandler handler = method.getAnnotation(GatewayMessageHandler.class);
            if (handler == null) {
                continue;
            }
            if (handler.sources() == null) {
                throw new GatewayInitilizationException("Method '" + method + "' with anootation "
                        + GatewayMessageHandler.class + " must have one or more " + NetworkServiceSource.class
                        + ". Check " + clas.getName() + " gateway service");
            }
            NetworkServiceSource[] origins = handler.sources();
            if (handler.sources().length == 0) {
                origins = NetworkServiceSource.values();
            }
            if (handler.value() == null || handler.value().isEmpty()) {
                throw new GatewayInitilizationException("Method '" + method + "' with anootation "
                        + GatewayMessageHandler.class + " must have associated an event (value). Check "
                        + clas.getName() + " gateway service");
            }
            if (method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(GatewayMessageContext.class)) {
                logger.info(
                        "Reading message event handler for event '" + handler.value() + "' in method '" + method + "'");
                element.getEventMethodMap().put(handler.value(),
                        new MethodParameterType(method, ccl.loadClass(
                                ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0]
                                        .getTypeName()),
                                origins));
            } else {
                throw new GatewayInitilizationException(
                        "Method '" + method + "' with anootation " + GatewayMessageHandler.class
                                + " must have associated 1 parameter type " + GatewayMessageContext.class);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void searchPluginsInvocation(Class<?> clas, final InternalElement<?> element, CustomClassLoader ccl)
            throws Exception {
        for (Field field : clas.getDeclaredFields()) {
            if (!field.isAnnotationPresent(GatewayPlugin.class)) {
                continue;
            }
            if (!(field.getDeclaringClass().isAnnotationPresent(GatewayService.class)
                    || field.getDeclaringClass().isAnnotationPresent(AuthenticationService.class))) {
                continue;
            }
            GatewayPlugin annotationPlugin = field.getAnnotation(GatewayPlugin.class);
            // Gateway plugin validation
            if (!this.gatewayPluginsMap.containsKey(annotationPlugin.value())) {
                throw new GatewayInitilizationException("There is no plugin called '" + annotationPlugin.value()
                        + "'. Check the '" + field.getDeclaringClass() + "' class attribute '" + field.getName() + "'");
            }
            logger.info("Reading plugin invocation '" + annotationPlugin.value() + "' for field '" + field.getName()
                    + "' into '" + element.getInstanceableClass() + "' class");
            // Add field for plugin injection
            element.getPluginFieldsList().add(new PluginField(field, field.getType(), annotationPlugin.value()));
        }
    }

    public Map<String, InternalAuthenticationService> getMessagingAuthServicesMap() {
        return messagingAuthServicesMap;
    }

    public Map<String, InternalService> getGatewayServicesMap() {
        return gatewayServicesMap;
    }

    public Map<String, List<InternalMessageFilter<?>>> getMessageFilterEventsMap() {
        return messageFilterEventsMap;
    }

    public Map<String, InternalPlugin> getGatewayPluginsMap() {
        return gatewayPluginsMap;
    }

    public PropertiesInitializer getPropertiesInicializer() {
        return propertiesInicializer;
    }
}
