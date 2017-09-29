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

import java.util.Set;

import cl.io.gateway.auth.IAuthenticationService;
import cl.io.gateway.messaging.NetworkServiceSource;

public class InternalAuthenticationService extends InternalElement<IAuthenticationService> {

    private final Set<String> protocolEvents;

    private final NetworkServiceSource origin;

    @SuppressWarnings("unchecked")
    public InternalAuthenticationService(final Set<String> protocolEvents, final NetworkServiceSource origin,
            String contextId, Class<? extends IAuthenticationService> serviceClass, String gatewayId) {
        super(contextId, (Class<IAuthenticationService>) serviceClass, gatewayId);
        this.protocolEvents = protocolEvents;
        this.origin = origin;
    }

    public Set<String> getProtocolEvents() {
        return protocolEvents;
    }

    public NetworkServiceSource getOrigin() {
        return origin;
    }
}
