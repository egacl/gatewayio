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

import cl.io.gateway.InternalService.MethodParameterType;
import cl.io.gateway.auth.AuthenticationService;
import cl.io.gateway.auth.IAuthenticationService;
import cl.io.gateway.exception.GatewayInitilizationException;
import cl.io.gateway.messaging.GatewayMessageContext;
import cl.io.gateway.messaging.GatewayMessageFilter;
import cl.io.gateway.messaging.GatewayMessageHandler;
import cl.io.gateway.messaging.IGatewayMessageFilter;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.service.GatewayService;
import cl.io.gateway.service.IGatewayService;

@SuppressWarnings("unchecked")
public class EnvironmentReader {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentReader.class);

    private final PropertiesInitializer propertiesInicializer;

    private final Map<String, InternalAuthenticationService> messagingAuthServicesMap;

    private final Map<String, InternalService> gatewayServicesMap;

    private final Map<String, List<InternalMessageFilter<?>>> messageFilterEventsMap;

    EnvironmentReader(PropertiesInitializer propertiesInicializer) {
        this.propertiesInicializer = propertiesInicializer;
        this.messagingAuthServicesMap = new HashMap<String, InternalAuthenticationService>();
        this.gatewayServicesMap = new HashMap<String, InternalService>();
        this.messageFilterEventsMap = new HashMap<String, List<InternalMessageFilter<?>>>();
    }

    public NetworkServiceManager createNetworkServiceManager(Gateway gateway, NetworkServiceSource origin) {
        InternalAuthenticationService authServ = this.messagingAuthServicesMap.get(origin.name());
        return new NetworkServiceManager(gateway, propertiesInicializer.getNetworkConfigurationMap().get(origin.name()),
                authServ);
    }

    public void filtersInitializer() throws Exception {
        for (String serviceId : this.propertiesInicializer.getResourcesLoader().getServicesId()) {
            this.getGatewayFilters(serviceId);
        }
    }

    public void servicesAndHandlersInitializer() throws Exception {
        for (String serviceId : this.propertiesInicializer.getResourcesLoader().getServicesId()) {
            this.getGatewayServicesAndHandlers(serviceId);
        }
    }

    public void pluginsInitializer() throws Exception {
        // TODO
    }

    public void networkInitializer() throws Exception {
        // Find authentication services for all networks origins
        for (String serviceId : this.propertiesInicializer.getResourcesLoader().getServicesId()) {
            this.getAuthenticationService(serviceId);
        }
        // Validation for default authentication service implementation
        for (NetworkServiceSource origin : NetworkServiceSource.values()) {
            if (!this.messagingAuthServicesMap.containsKey(origin.name())) {
                // add default authenticacion
                logger.info("Network service for '" + origin
                        + "' origin implements default authentication service (no authentication)");
                this.messagingAuthServicesMap.put(origin.name(),
                        new InternalAuthenticationService(DefaultAuthenticationService.class, null, origin, null));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void getGatewayFilters(String serviceId) throws Exception {
        CustomClassLoader ccl = (CustomClassLoader) this.propertiesInicializer.getResourcesLoader()
                .getServiceClassLoader(serviceId);
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
                        messageType, serviceId));
            } else {
                throw new GatewayInitilizationException(
                        "'" + clas + "' must implements " + IGatewayService.class + " interface");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void getGatewayServicesAndHandlers(String serviceId) throws Exception {
        CustomClassLoader ccl = (CustomClassLoader) this.propertiesInicializer.getResourcesLoader()
                .getServiceClassLoader(serviceId);
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
                                    + " and " + this.gatewayServicesMap.get(servAttibs.value()).getServiceClass());
                }
                logger.info("Reading '" + servAttibs.value() + "' represented by class " + clas);
                final InternalService service = new InternalService(serviceId, (Class<IGatewayService>) clas,
                        servAttibs.value());
                this.gatewayServicesMap.put(servAttibs.value(), service);
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
                        logger.info("Reading message event handler for event '" + handler.value() + "' in method '"
                                + method + "'");
                        service.getEventMethodMap().put(handler.value(), new MethodParameterType(method, ccl.loadClass(
                                ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0]
                                        .getTypeName()),
                                origins));
                    } else {
                        throw new GatewayInitilizationException(
                                "Method '" + method + "' with anootation " + GatewayMessageHandler.class
                                        + " must have associated 1 parameter type " + GatewayMessageContext.class);
                    }
                }
            } else {
                throw new GatewayInitilizationException(
                        "'" + clas + "' must implements " + IGatewayService.class + " interface");
            }
        }
    }

    private void getAuthenticationService(String serviceId) throws Exception {
        CustomClassLoader ccl = (CustomClassLoader) this.propertiesInicializer.getResourcesLoader()
                .getServiceClassLoader(serviceId);
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
                                    + this.messagingAuthServicesMap.get(origin.name()).getInstanceClass());
                }
                Set<String> authProtocolsEvents = new HashSet<String>();
                if (authAttibs.authProtocolEvents() != null) {
                    authProtocolsEvents.addAll(Arrays.asList(authAttibs.authProtocolEvents()));
                }
                // Add authentication service
                logger.info("Reading network service for '" + origin + "' origin implements '" + clas
                        + "' authentication service for these protocols events: "
                        + Arrays.toString(authProtocolsEvents.toArray(new String[0])));
                this.messagingAuthServicesMap.put(origin.name(), new InternalAuthenticationService(
                        (Class<IAuthenticationService>) clas, authProtocolsEvents, origin, serviceId));
            } else {
                throw new GatewayInitilizationException(
                        "'" + clas + "' must implements " + IAuthenticationService.class + " interface");
            }
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
}
