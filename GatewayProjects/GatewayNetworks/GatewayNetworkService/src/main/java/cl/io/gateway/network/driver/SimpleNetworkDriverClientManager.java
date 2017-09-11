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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.network.IConnectionStatus;
import cl.io.gateway.network.NetworkConnection;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

/**
 * Connection manager, which allows managing connections to other network
 * servers.
 *
 * @author egacl
 */
public class SimpleNetworkDriverClientManager implements INetworkDriverClientManager {

    private static final Logger logger = LoggerFactory.getLogger(SimpleNetworkDriverClientManager.class);

    /**
     * Reference to network driver instance
     */
    private final AbstractNetworkDriver networkDriver;

    /**
     * Connection data for every external channel id server
     */
    private final ConcurrentHashMap<String, DriverClientNetworkConnection> connections;

    /**
     * Network driver implementations interfaces for bootstrap to initialize a
     * connection to another server in the network
     */
    private final IClientChannelInitializer<Channel> initializer;

    /**
     * Thread pool executor for clients connections creation
     */
    private final ExecutorService connectionExecutor;

    public SimpleNetworkDriverClientManager(AbstractNetworkDriver networkDriver,
            final IClientChannelInitializer<Channel> initializer) {
        this.networkDriver = networkDriver;
        this.connections = new ConcurrentHashMap<>(50, 0.5f);
        this.initializer = initializer;
        this.connectionExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void connect(final NetworkConnection connection, final IConnectionStatus connStatus) {
        if (this.connections.get(connection.getChannelId()) == null) {
            final DriverClientNetworkConnection driverConnection = new DriverClientNetworkConnection(connection,
                    this.networkDriver.getConfiguration().getMaxTimeOuts());
            this.connections.put(connection.getChannelId(), driverConnection);
            this.connectionExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    SimpleNetworkDriverClientManager.this.createConnection(driverConnection, connStatus);
                }
            });
        } else {
            String message = "Already exists a connection channel id: " + connection.getChannelId();
            logger.warn(message);
            if (connStatus != null) {
                connStatus.error(connection.getChannelId(), new NetworkDriverException(message));
            }
        }
    }

    @Override
    public void start() {
        // not necessary to implement
    }

    @Override
    public void stop() {
        this.connectionExecutor.shutdown();
    }

    @Override
    public void initialice() {
        // not necessary to implement
    }

    @Override
    public void scheduledReconnect(final DriverClientNetworkConnection connection) {
        this.connectionExecutor.execute(new Runnable() {

            @Override
            public void run() {
                SimpleNetworkDriverClientManager.this.createConnection(connection, null);
            }
        });
    }

    /**
     * Allows to invoke the implementation of the network driver with the specific
     * logic to initialize connection to another network server.
     *
     * @param connection
     *            connection data
     * @param connStatus
     *            listener for connection status notification
     */
    private void createConnection(final DriverClientNetworkConnection connection, final IConnectionStatus connStatus) {
        try {
            final Bootstrap boostrap = this.initializer.createBootstrapClientConnection(connection);
            boostrap.connect(connection.getActualUrl().getUri().getHost(), connection.getActualUrl().getUri().getPort())
                    .sync();
            if (connStatus != null) {
                connStatus.success(connection.getChannelId());
            }
        } catch (Throwable err) {
            String msg = "Error creating client connection " + connection;
            logger.error(msg, err);
            if (connStatus != null) {
                connStatus.error(connection.getChannelId(), new Throwable(msg, err));
            }
        }
    }
}
