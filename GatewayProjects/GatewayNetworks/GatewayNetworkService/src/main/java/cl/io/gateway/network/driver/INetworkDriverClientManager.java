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

import cl.io.gateway.network.IConnectionStatus;
import cl.io.gateway.network.NetworkConnection;

/**
 * Interface that represents the connection manager, which allows managing
 * connections to other network servers.
 *
 * @author egacl
 */
public interface INetworkDriverClientManager extends INetworkDriverServer {

    /**
     * Connect to another server on the network.
     *
     * @param connection
     *            network connection data
     * @param connStatus
     *            connection status callback
     */
    void connect(NetworkConnection connection, IConnectionStatus connStatus);

    /**
     * When a connection is dropped, this method allows reconnection attempts to the
     * network server.
     *
     * @param connection
     *            network connection context
     */
    void scheduledReconnect(DriverClientNetworkConnection connection);
}
