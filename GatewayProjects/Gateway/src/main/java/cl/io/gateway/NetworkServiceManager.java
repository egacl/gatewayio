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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.auth.AuthenticationStatus;
import cl.io.gateway.auth.IAuthenticationGatewayNetworkService;
import cl.io.gateway.auth.IAuthenticationStatusListener;
import cl.io.gateway.exception.GatewayProcessException;
import cl.io.gateway.messaging.IGatewayMessageFilter;
import cl.io.gateway.messaging.IGatewayMessageHandler;
import cl.io.gateway.messaging.IGatewayNetworkService;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.IDeliveryStatusListener;
import cl.io.gateway.network.INetworkService;
import cl.io.gateway.network.NetworkConfiguration;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.NetworkService;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.network.handler.INetworkEventListener;
import cl.io.gateway.network.handler.INetworkMessageHandler;
import cl.io.gateway.vo.GatewayClient;

@SuppressWarnings("unchecked")
public class NetworkServiceManager implements IGatewayNetworkService, IAuthenticationGatewayNetworkService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkServiceManager.class);

    /**
     * Instancia a gateway
     */
    private final Gateway gateway;

    /**
     * Servicio de mensajeria de red
     */
    private final INetworkService networkService;

    /**
     * Mapa de clientes conectados al gateway identificados por su channelId
     */
    private final ConcurrentMap<String, InternalGatewaySession> connectedClientsMap;

    /**
     * Mapa de handlers de aplicaciones/servicios para eventos de mensajeria de red
     */
    private final ConcurrentMap<String, IGatewayMessageHandler<?>> eventSubscriptionMap;

    /**
     * Mapa de filtros de red para los diferentes eventos enviados y recibidos.
     */
    private final ConcurrentMap<String, List<InternalMessageFilter<?>>> eventFiltersMap;

    /**
     * Listado que contiene handlers para eventos de estado de conexion de sesiones
     */
    private final List<IAuthenticationStatusListener> authenticationStatusHandlerList;

    /**
     * Servicio de autenticacion de sesiones.
     */
    private final InternalAuthenticationService authenticationService;

    private final NetworkServiceSource origin;

    public NetworkServiceManager(final Gateway gateway, NetworkConfiguration clientNetConf,
            InternalAuthenticationService authenticationService) {
        this.gateway = gateway;
        this.networkService = new NetworkService(clientNetConf);
        this.authenticationService = authenticationService;
        this.origin = this.authenticationService.getOrigin();
        this.connectedClientsMap = new ConcurrentHashMap<String, InternalGatewaySession>(100, 0.6F);
        this.eventSubscriptionMap = new ConcurrentHashMap<String, IGatewayMessageHandler<?>>(100, 0.6F);
        this.eventFiltersMap = new ConcurrentHashMap<String, List<InternalMessageFilter<?>>>(50, 0.8F);
        this.authenticationStatusHandlerList = new LinkedList<IAuthenticationStatusListener>();
    }

    void init() throws Exception {
        this.authenticationService.init(this);
    }

    void start() throws Exception {
        this.networkService.start();
    }

    public NetworkServiceSource getOrigin() {
        return origin;
    }

    @Override
    public void clientAuthenticated(final GatewayClient client, final AuthenticationStatus status) {
        InternalGatewaySession session = this.connectedClientsMap.get(client.getChannelID());
        if (session == null) {
            session = new InternalGatewaySession(client);
            this.connectedClientsMap.put(client.getChannelID(), session);
        }
        session.setStatus(status);
        // Se notifica cambio en estado de autenticacion
        this.broadcastNetworkEvent(session.toPublicSession(this.origin), status);
        if (status == AuthenticationStatus.LOGGED_OUT) {
            this.connectedClientsMap.remove(client.getChannelID());
        }
    }

    @Override
    public <T> void addMessageHandler(final String event, final IGatewayMessageHandler<T> handler)
            throws GatewayProcessException {
        IGatewayMessageHandler<T> eventSubs = (IGatewayMessageHandler<T>) this.eventSubscriptionMap.get(event);
        if (eventSubs == null) {
            // se agrega handler para gateway y para servicio de mensajeria de red
            this.eventSubscriptionMap.put(event, handler);
            this.networkService.addMessageHandler(event, new INetworkMessageHandler<T>() {

                @Override
                public void onMessage(final NetworkMessage<T> message) throws Exception {
                    NetworkServiceManager.this.processNetworkMessage(message);
                }
            });
        } else {
            throw new GatewayProcessException("There is already a handler for event '" + event + "'");
        }
    }

    @Override
    public void removeMessageHandler(final String event) {
        if (this.eventSubscriptionMap.remove(event) != null) {
            this.networkService.removeMessageHandler(event);
        }
    }

    @Override
    public <T> void sendNetworkMessage(final IGatewayClientSession client, final NetworkMessage<T> message)
            throws NetworkDriverException {
        this.sendNetworkMessage(client, message, null);
    }

    @Override
    public <T> void sendNetworkMessage(final IGatewayClientSession session, final NetworkMessage<T> message,
            final IDeliveryStatusListener deliveryStatus) throws NetworkDriverException {
        // Se filtra el mensaje
        boolean filterResponse = this.filterMessage(message, session, false);
        if (filterResponse) {
            this.networkService.send(session.getClient().getChannelID(), message, deliveryStatus);
        } else if (deliveryStatus != null) {
            deliveryStatus.error(session.getClient().getChannelID(), message,
                    new GatewayProcessException("Filters rejects this message"));
        }
    }

    @Override
    public void addAuthenticationStatusListener(IAuthenticationStatusListener handler) {
        synchronized (this.authenticationStatusHandlerList) {
            this.authenticationStatusHandlerList.add(handler);
        }
    }

    @Override
    public boolean removeAuthenticationStatusListener(IAuthenticationStatusListener handler) {
        synchronized (this.authenticationStatusHandlerList) {
            return this.authenticationStatusHandlerList.remove(handler);
        }
    }

    @Override
    public void addNetworkEventHandler(final INetworkEventListener handler) {
        this.networkService.addNetworkEventHandler(handler);
    }

    @Override
    public boolean removeNetworkEventHandler(final INetworkEventListener handler) {
        return this.networkService.removeNetworkEventHandler(handler);
    }

    private <T> void processNetworkMessage(final NetworkMessage<T> message) throws Exception {
        final InternalGatewaySession session = this.connectedClientsMap.get(message.getOriginChannelId());
        final IGatewayMessageHandler<T> handler = (IGatewayMessageHandler<T>) this.eventSubscriptionMap
                .get(message.getEvent());
        if (session == null) {
            throw new GatewayProcessException("Message sender is doesn't exists");
        }
        if (handler == null) {
            throw new GatewayProcessException("Message handler is doesn't exists");
        }
        if (this.isValidToProcessMessage(session, message.getEvent())) {
            // Se filtra el mensaje
            boolean filterResponse = this.filterMessage(message, session.toPublicSession(this.origin), true);
            // Se entrega el mensaje al handler para que sea procesado
            if (filterResponse) {
                handler.onMessage(message, session.toPublicSession(this.origin));
            }
        } else {
            logger.error("Client is not properly authenticated: " + session + ", message: " + message);
            logger.error("Allowed protocols events are: " + this.authenticationService.getProtocolEvents());
            throw new GatewayProcessException(
                    "Client is not properly authenticated: " + session + ", message: " + message);
        }
    }

    private boolean isValidToProcessMessage(final InternalGatewaySession session, final String event) {
        if (session.getStatus() == AuthenticationStatus.LOGGED_IN) {
            return true;
        }
        return (session.getStatus() == AuthenticationStatus.PROCESS_LOGGING
                && this.authenticationService.getProtocolEvents().contains(event));
    }

    private <T> boolean filterMessage(final NetworkMessage<T> message, final IGatewayClientSession session,
            final boolean isRequest) {
        synchronized (this.eventFiltersMap) {
            // Se obtienen los filtros asociados al evento recibido
            final List<InternalMessageFilter<?>> filters = this.eventFiltersMap.get(message.getEvent());
            boolean filterResponse = true;
            if (filters != null && !filters.isEmpty()) {
                filterResponse = false;
                for (final InternalMessageFilter<?> f : filters) {
                    try {
                        filterResponse = this.doFilter((IGatewayMessageFilter<T>) f.getFilter(), message, session,
                                isRequest);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Filter priority '" + f.getPriority() + "' for event '" + f.getEvent()
                                    + "' responses '" + filterResponse + "' for message: " + message);
                        }
                    } catch (Throwable e) {
                        logger.error("Error processing doFilter for event: " + message.getEvent() + ", message: "
                                + message + " and filter : " + f.getFilter(), e);
                        filterResponse = false;
                        try {
                            this.onError((IGatewayMessageFilter<T>) f.getFilter(), message, session, e);
                        } catch (Throwable ee) {
                            // oh god!
                            logger.error("Error processing onError for event: " + message.getEvent() + ", message: "
                                    + message + " and filter : " + f.getFilter(), ee);
                        }
                    }
                    if (!filterResponse) {
                        logger.error("Ending filters loop for message: " + message + " and client " + session);
                        break;
                    }
                }
            }
            return filterResponse;
        }
    }

    private <T> boolean doFilter(final IGatewayMessageFilter<T> filter, final NetworkMessage<T> message,
            final IGatewayClientSession client, final boolean isRequest) throws Throwable {
        if (isRequest) {
            return filter.doFilterRequest(message, client);
        }
        return filter.doFilterResponse(message, client);
    }

    private <T> void onError(final IGatewayMessageFilter<T> filter, final NetworkMessage<T> message,
            final IGatewayClientSession client, Throwable err) throws Throwable {
        filter.onError(message, client, err);
    }

    private void broadcastNetworkEvent(final GatewayClientSession session, final AuthenticationStatus status) {
        synchronized (this.authenticationStatusHandlerList) {
            for (final IAuthenticationStatusListener eventHandler : this.authenticationStatusHandlerList) {
                try {
                    eventHandler.onStatusChanged(session, status);
                } catch (Throwable err) {
                    logger.error("Error reporting authentication status event", err);
                }
            }
        }
    }

    INetworkService getNetworkService() {
        return this.networkService;
    }

    @SuppressWarnings("rawtypes")
    void addMessageFilter(final InternalMessageFilter filter) {
        synchronized (this.eventFiltersMap) {
            List<InternalMessageFilter<?>> filtersEvent = this.eventFiltersMap.get(filter.getEvent());
            if (filtersEvent == null) {
                filtersEvent = new ArrayList<InternalMessageFilter<?>>();
                this.eventFiltersMap.put(filter.getEvent(), filtersEvent);
            }
            filtersEvent.add(filter);
            Collections.sort(filtersEvent, new Comparator<InternalMessageFilter>() {

                @Override
                public int compare(InternalMessageFilter a, InternalMessageFilter b) {
                    return a.compareTo(b);
                }
            });
        }
    }

    boolean removeMessageFilter(final String event) {
        synchronized (this.eventFiltersMap) {
            return this.eventFiltersMap.remove(event) != null;
        }
    }
}
