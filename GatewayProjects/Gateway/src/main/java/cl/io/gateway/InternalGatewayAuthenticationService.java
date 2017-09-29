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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.auth.IAuthenticationGatewayNetworkService;
import cl.io.gateway.auth.IAuthenticationService;
import cl.io.gateway.messaging.NetworkServiceSource;

public class InternalGatewayAuthenticationService
        extends AbstractGateway<IAuthenticationService, InternalAuthenticationService> {

    private static final Logger logger = LoggerFactory.getLogger(InternalGatewayAuthenticationService.class);

    private final Set<String> protocolEvents;

    InternalGatewayAuthenticationService(InternalAuthenticationService element) throws Exception {
        super(Gateway.getInstance(), element);
        if (this.getProtocolEvents() == null) {
            this.protocolEvents = new HashSet<String>();
        } else {
            this.protocolEvents = new HashSet<String>(this.getProtocolEvents());
        }
    }

    public void init(IAuthenticationGatewayNetworkService networkService) throws Exception {
        logger.info("Initializing authentication service '" + this.getElementInstanceClass() + "' from '"
                + this.getContextId() + "' context and '" + this.getOrigin() + "' network service origin");
        super.init();
        this.getElementInstance().initialize(this, networkService);
    }

    public Set<String> getProtocolEvents() {
        return ((InternalAuthenticationService) this.getElement()).getProtocolEvents();
    }

    public NetworkServiceSource getOrigin() {
        return ((InternalAuthenticationService) this.getElement()).getOrigin();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InternalGatewayAuthenticationService [protocolEvents=");
        builder.append(protocolEvents);
        builder.append(", getGateway()=");
        builder.append(getGateway());
        builder.append(", getContextId()=");
        builder.append(getContextId());
        builder.append(", getGatewayServiceId()=");
        builder.append(getGatewayId());
        builder.append(", getEventsHandlerMap()=");
        builder.append(getEventsHandlerMap());
        builder.append(", getServiceInstance()=");
        builder.append(getElementInstance());
        builder.append(", getInstanceClass()=");
        builder.append(getElementInstanceClass());
        builder.append(", getElement()=");
        builder.append(getElement());
        builder.append(", getClass()=");
        builder.append(getClass());
        builder.append(", hashCode()=");
        builder.append(hashCode());
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}
