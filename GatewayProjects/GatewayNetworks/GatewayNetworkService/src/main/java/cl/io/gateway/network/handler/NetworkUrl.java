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
package cl.io.gateway.network.handler;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Network url.
 * 
 * @author egacl
 */
public class NetworkUrl {

    private final String url;

    private final URI uri;

    public NetworkUrl(String url) throws URISyntaxException {
        this.url = url;
        this.uri = new URI(url);
    }

    public String getUrl() {
        return url;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NetworkUrl{");
        sb.append("url='").append(url).append('\'');
        sb.append(", uri=").append(uri);
        sb.append('}');
        return sb.toString();
    }
}
