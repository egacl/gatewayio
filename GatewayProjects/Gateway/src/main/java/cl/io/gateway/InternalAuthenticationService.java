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

public class InternalAuthenticationService extends AbstractGateway<IAuthenticationService> {

    private static final Logger logger = LoggerFactory.getLogger(InternalAuthenticationService.class);

    private final Class<? extends IAuthenticationService> authenticationServiceClass;

    private final Set<String> protocolEvents;

    private final NetworkServiceSource origin;

    private IAuthenticationService instance;

    @SuppressWarnings("unchecked")
    InternalAuthenticationService(Class<? extends IAuthenticationService> authenticationServiceClass,
            final Set<String> protocolEvents, final NetworkServiceSource origin, final String serviceId)
            throws Exception {
        super(Gateway.getInstance(), serviceId, origin.name(),
                (Class<IAuthenticationService>) authenticationServiceClass);
        this.authenticationServiceClass = authenticationServiceClass;
        if (protocolEvents == null) {
            this.protocolEvents = new HashSet<String>();
        } else {
            this.protocolEvents = new HashSet<String>(protocolEvents);
        }
        this.origin = origin;
    }

    public synchronized void init(IAuthenticationGatewayNetworkService networkService) throws Exception {
        if (this.instance == null) {
            logger.info("Initializing authentication service ' + authenticationServiceClass + ' from '"
                    + this.getServiceId() + "' context and '" + origin + "' network service origin");
            this.instance = authenticationServiceClass.newInstance();
            this.instance.initialize(this, networkService);
        }
    }

    public Set<String> getProtocolEvents() {
        return protocolEvents;
    }

    public NetworkServiceSource getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InternalAuthenticationService [authenticationServiceClass=");
        builder.append(authenticationServiceClass);
        builder.append(", protocolEvents=");
        builder.append(protocolEvents);
        builder.append(", origin=");
        builder.append(origin);
        builder.append(", instance=");
        builder.append(instance);
        builder.append("]");
        return builder.toString();
    }
}
