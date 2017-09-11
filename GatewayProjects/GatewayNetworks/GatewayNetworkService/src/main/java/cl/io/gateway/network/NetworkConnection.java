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

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cl.io.gateway.network.handler.NetworkUrl;

/**
 * Class that allows to define the attributes necessary to establish a network
 * connection with some channel. Instances of this class are used by the network
 * driver to establish a connection.
 *
 * @author egacl
 */
public class NetworkConnection {

    /**
     * Connecction context data.
     */
    private Map<String, Object> context;

    /**
     * channel id
     */
    private final String channelId;

    /**
     * URLs to connect
     */
    private final List<NetworkUrl> url;

    /**
     * use ssl
     */
    private final boolean ssl;

    public NetworkConnection(String channelId, String url) throws URISyntaxException {
        this(channelId, url, false);
    }

    public NetworkConnection(String channelId, String url, boolean ssl) throws URISyntaxException {
        this.url = new LinkedList<>();
        this.url.add(new NetworkUrl(url));
        this.channelId = channelId;
        this.ssl = ssl;
    }

    public NetworkConnection addUrl(NetworkUrl url) {
        this.url.add(url);
        return this;
    }

    public List<NetworkUrl> getUrl() {
        return this.url;
    }

    public String getChannelId() {
        return channelId;
    }

    public Map<String, Object> context(String id, Object value) {
        this.context.put(id, value);
        return this.context;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public boolean isSsl() {
        return ssl;
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder;
        toStringBuilder = new StringBuilder("NetworkConnection{");
        toStringBuilder.append("channelID=").append(this.channelId);
        toStringBuilder.append(",url=").append(this.url);
        toStringBuilder.append(",context=").append(this.context);
        toStringBuilder.append('}');
        return toStringBuilder.toString();
    }
}
