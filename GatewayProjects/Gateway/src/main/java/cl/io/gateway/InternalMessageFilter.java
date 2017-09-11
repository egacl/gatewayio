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

import cl.io.gateway.messaging.IGatewayMessageFilter;

public final class InternalMessageFilter<T> implements Comparable<InternalMessageFilter<T>> {

    private final Integer priority;

    private final String event;

    private IGatewayMessageFilter<T> filter;

    private final Class<IGatewayMessageFilter<T>> filterClass;

    private final Class<T> messageType;

    private final String serviceId;

    public InternalMessageFilter(final int priority, final String event,
            final Class<IGatewayMessageFilter<T>> filterClass, final Class<T> messageType, String serviceId) {
        this.priority = priority;
        this.event = event;
        this.filterClass = filterClass;
        this.messageType = messageType;
        this.serviceId = serviceId;
    }

    public InternalMessageFilter(InternalMessageFilter<T> filter) {
        this.priority = filter.priority;
        this.event = filter.event;
        this.filterClass = filter.filterClass;
        this.messageType = filter.messageType;
        this.serviceId = filter.serviceId;
    }

    public synchronized void init() throws Exception {
        if (this.filter == null) {
            this.filter = filterClass.newInstance();
        }
    }

    public int getPriority() {
        return priority;
    }

    public String getEvent() {
        return event;
    }

    public IGatewayMessageFilter<T> getFilter() {
        return filter;
    }

    public Class<IGatewayMessageFilter<T>> getFilterClass() {
        return filterClass;
    }

    public Class<T> getMessageType() {
        return messageType;
    }

    public String getServiceId() {
        return serviceId;
    }

    @Override
    public int compareTo(InternalMessageFilter<T> o) {
        if (o == this) {
            return 0;
        }
        return this.priority.compareTo(o.priority);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InternalMessageFilter [priority=");
        builder.append(priority);
        builder.append(", event=");
        builder.append(event);
        builder.append(", filter=");
        builder.append(filter);
        builder.append(", filterClass=");
        builder.append(filterClass);
        builder.append(", messageType=");
        builder.append(messageType);
        builder.append(", serviceId=");
        builder.append(serviceId);
        builder.append("]");
        return builder.toString();
    }
}
