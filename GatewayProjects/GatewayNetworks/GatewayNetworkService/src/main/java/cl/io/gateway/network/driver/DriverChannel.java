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
package cl.io.gateway.network.driver;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.Channel;

/**
 * A class that maintains the state of a connection, such as the amount of
 * timeouts and number of messages related to this channel.
 *
 * @author egacl
 */
public class DriverChannel {

    private final Channel channel;

    private final String channelId;

    private AtomicInteger reconnectCounter = new AtomicInteger(0);

    private AtomicLong channelMessageSequence = new AtomicLong(0);

    public DriverChannel(final String channelId, final Channel channel) {
        this.channelId = channelId;
        this.channel = channel;
    }

    public int addAndGetReconnectCounter() {
        return reconnectCounter.incrementAndGet();
    }

    public long addAndGetMessageCounter() {
        return this.channelMessageSequence.incrementAndGet();
    }

    public void resetReconnectCounter() {
        this.reconnectCounter.set(0);
    }

    public int getReconnectCounter() {
        return reconnectCounter.get();
    }

    public Channel getChannel() {
        return channel;
    }

    public String getChannelId() {
        return channelId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DriverChannel{");
        sb.append("channel=").append(channel);
        sb.append(", channelId='").append(channelId).append('\'');
        sb.append(", reconnectCounter=").append(reconnectCounter);
        sb.append('}');
        return sb.toString();
    }
}
