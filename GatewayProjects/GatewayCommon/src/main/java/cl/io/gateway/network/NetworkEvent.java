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
package cl.io.gateway.network;

/**
 *
 * @author egacl
 */
public class NetworkEvent {
    
    private final String channelId;
    
    private final NetworkEventType eventType;
    
    public NetworkEvent(final String channelId, final NetworkEventType eventType) {
        this.channelId = channelId;
        this.eventType = eventType;
    }

    public String getChannelId() {
        return channelId;
    }

    public NetworkEventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder;
        toStringBuilder = new StringBuilder("NetworkEvent{");
        toStringBuilder.append("channelId=").append(this.channelId);
        toStringBuilder.append(",eventType=").append(this.eventType);
        toStringBuilder.append('}');
        return toStringBuilder.toString();
    }
    
}
