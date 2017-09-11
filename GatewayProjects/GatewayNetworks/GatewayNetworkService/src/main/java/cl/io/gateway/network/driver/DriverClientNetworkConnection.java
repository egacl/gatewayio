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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.network.NetworkConnection;
import cl.io.gateway.network.handler.NetworkUrl;

/**
 *
 * @author egacl
 *
 */
public class DriverClientNetworkConnection {

    private final Logger logger = LoggerFactory.getLogger(DriverClientNetworkConnection.class);

    private int reconnectCounter;

    private int connectionUrlIndex;

    private final NetworkConnection connection;

    private final int maxReconnect;

    public DriverClientNetworkConnection(final NetworkConnection connection, final int maxReconnect) {
        this.connection = connection;
        this.reconnectCounter = -1;
        this.connectionUrlIndex = 0;
        this.maxReconnect = maxReconnect;
    }

    public String getChannelId() {
        return this.connection.getChannelId();
    }

    public NetworkUrl getUrlToConnect() {
        // Check availables URLs
        if (this.connectionUrlIndex >= this.connection.getUrl().size()) {
            logger.warn("Reset URL index to 0 (from %n)", this.connection.getUrl().size());
            this.connectionUrlIndex = 0;
        }
        // Increment reconnect counter
        this.reconnectCounter++;
        if (this.reconnectCounter >= this.maxReconnect) {
            this.reconnectCounter = 0;
            this.connectionUrlIndex++;
            logger.warn("Change connection URL");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Connecting to %s reconnect counter %n", this.getActualUrl(), this.reconnectCounter);
        }
        return this.getActualUrl();
    }

    public NetworkUrl getActualUrl() {
        // Check availables URLs
        if (this.connectionUrlIndex >= this.connection.getUrl().size()) {
            logger.warn("Reset URL index to 0 (from %n)", this.connection.getUrl().size());
            this.connectionUrlIndex = 0;
        }
        return this.connection.getUrl().get(this.connectionUrlIndex);
    }

    public NetworkConnection getConnection() {
        return connection;
    }

    public int getReconnectCounter() {
        return reconnectCounter;
    }
}
