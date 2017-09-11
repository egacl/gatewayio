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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.service.IGatewayService;

@SuppressWarnings("rawtypes")
public class InternalService {

    private final String serviceId;

    private final String gatewayServiceId;

    private final Class<? extends IGatewayService> serviceClass;

    private final Map<String, MethodParameterType> eventMethodMap;

    public InternalService(String serviceId, Class<? extends IGatewayService> serviceClass, String gatewayServiceId) {
        this.serviceId = serviceId;
        this.serviceClass = serviceClass;
        this.gatewayServiceId = gatewayServiceId;
        this.eventMethodMap = new HashMap<String, MethodParameterType>();
    }

    public String getServiceId() {
        return serviceId;
    }

    public Class<? extends IGatewayService> getServiceClass() {
        return serviceClass;
    }

    public String getGatewayServiceId() {
        return gatewayServiceId;
    }

    public Map<String, MethodParameterType> getEventMethodMap() {
        return eventMethodMap;
    }

    public static class MethodParameterType<T> {

        private final Method method;

        private final Class<T> parameterType;

        private final NetworkServiceSource[] origins;

        public MethodParameterType(final Method method, final Class<T> parameterType, NetworkServiceSource[] origins) {
            this.method = method;
            this.parameterType = parameterType;
            this.origins = origins;
        }

        public Method getMethod() {
            return method;
        }

        public Class<T> getParameterType() {
            return parameterType;
        }

        public NetworkServiceSource[] getOrigins() {
            return origins;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("MethodParameterType [method=");
            builder.append(method);
            builder.append(", parameterType=");
            builder.append(parameterType);
            builder.append(", origins=");
            builder.append(Arrays.toString(origins));
            builder.append("]");
            return builder.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InternalService [serviceId=");
        builder.append(serviceId);
        builder.append(", gatewayServiceId=");
        builder.append(gatewayServiceId);
        builder.append(", serviceClass=");
        builder.append(serviceClass);
        builder.append(", eventMethodMap=");
        builder.append(eventMethodMap);
        builder.append("]");
        return builder.toString();
    }
}
