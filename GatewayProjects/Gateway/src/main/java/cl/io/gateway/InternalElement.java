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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cl.io.gateway.messaging.NetworkServiceSource;

@SuppressWarnings("rawtypes")
public abstract class InternalElement<T> {

    private final String contextId;

    private final String gatewayId;

    private final Class<T> instanceableClass;

    private final Map<String, MethodParameterType> eventMethodMap;

    private final List<PluginField> pluginFieldsList;

    public InternalElement(String contextId, Class<T> instanceableClass, String gatewayId) {
        this.contextId = contextId;
        this.instanceableClass = instanceableClass;
        this.gatewayId = gatewayId;
        this.eventMethodMap = new HashMap<String, MethodParameterType>();
        this.pluginFieldsList = new LinkedList<PluginField>();
    }

    public String getContextId() {
        return contextId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public Class<T> getInstanceableClass() {
        return instanceableClass;
    }

    public Map<String, MethodParameterType> getEventMethodMap() {
        return eventMethodMap;
    }

    public List<PluginField> getPluginFieldsList() {
        return pluginFieldsList;
    }

    public static class PluginField<T> {

        private final Field field;

        private final Class<T> pluginFieldType;

        private final String pluginId;

        public PluginField(Field field, Class<T> pluginFieldType, String pluginId) {
            this.field = field;
            this.pluginFieldType = pluginFieldType;
            this.pluginId = pluginId;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PluginField [field=");
            builder.append(field);
            builder.append(", pluginFieldType=");
            builder.append(pluginFieldType);
            builder.append(", pluginId=");
            builder.append(pluginId);
            builder.append("]");
            return builder.toString();
        }

        public Field getField() {
            return field;
        }

        public Class<T> getPluginFieldType() {
            return pluginFieldType;
        }

        public String getPluginId() {
            return pluginId;
        }
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
        builder.append("InternalElement [contextId=");
        builder.append(contextId);
        builder.append(", gatewayId=");
        builder.append(gatewayId);
        builder.append(", instanceableClass=");
        builder.append(instanceableClass);
        builder.append(", eventMethodMap=");
        builder.append(eventMethodMap);
        builder.append(", pluginFieldsList=");
        builder.append(pluginFieldsList);
        builder.append("]");
        return builder.toString();
    }
}
