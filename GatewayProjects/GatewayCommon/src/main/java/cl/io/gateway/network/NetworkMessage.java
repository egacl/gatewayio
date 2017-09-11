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

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains a message received through the network.
 * @author egacl
 * @param <T> transported message java type.
 */
public class NetworkMessage<T extends Object> {
    
    public static final String CHANNEL_MESSAGE_SEQUENCE = "CHANNEL_MESSAGE_SEQUENCE";

    public static final String EVENT_MESSAGE_SEQUENCE = "EVENT_MESSAGE_SEQUENCE";
    
    private Map<String, Object> context = new HashMap<String, Object>();
    
    private String source;
    
    private String target;
    
    private String event;
    
    private String channelId;
    
    private T message;
    
    public NetworkMessage() {}
    
    public NetworkMessage(String event) {
        this.event = event;
    }
    
    public NetworkMessage(String event, T message) {
        this.event = event;
        this.message = message;
    }

    /**
     * @return the event
     */
    public String getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * @return the message
     */
    public T getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(T message) {
        this.message = message;
    }
    
    public Object putContext(String key, Object value) {
		return this.context.put(key, value);
}
    
    public Object getContextValue(String key) {
    		return this.context.get(key);
    }

    public Map<String, Object> getContextCopy() {
        return new HashMap<>(this.context);
    }
    
    public String getOriginChannelId() {
        return this.channelId;
    }
    
    public long getChannelMessageSequence() {
    	return (Long)this.context.get(CHANNEL_MESSAGE_SEQUENCE);
    }
    
    public long getEventMessageSequence() {
    	return (Long)this.context.get(EVENT_MESSAGE_SEQUENCE);
    }

    public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NetworkMessage [context=").append(context).append(", source=").append(source)
				.append(", target=").append(target).append(", event=").append(event).append(", channelId=")
				.append(channelId).append(", message=").append(message).append("]");
		return builder.toString();
	}

}
