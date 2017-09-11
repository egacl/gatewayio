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

import java.util.LinkedList;
import java.util.List;

import cl.io.gateway.network.driver.IEventMessageCodec;
import cl.io.gateway.network.driver.INetworkDriver;
import io.netty.handler.ssl.SslContext;

/**
 * Class that allows to define the properties necessary for the initialization
 * and behavior of the network driver.
 *
 * @author egacl
 */
@SuppressWarnings("rawtypes")
public class NetworkConfiguration {

    /**
     * accept clients connections or not
     */
    private boolean acceptClientsConnections;

    /**
     * listening port
     */
    private final int port;

    /**
     * bind address
     */
    private String ip;

    /**
     * connection path
     */
    private String path;

    /**
     * Making your server support SSL/TLS
     */
    private SslContext sslContext;

    /**
     * Message codec for serialization and deserealization message
     */
    private final List<IEventMessageCodec> codecs;

    /**
     * Network driver class
     */
    private Class<? extends INetworkDriver> driverClass;

    /**
     * max timeouts quantity for close connection
     */
    private int maxTimeOuts = 3;

    /**
     * iddle seconds
     */
    private int iddleTimeInSeconds = 10;

    public NetworkConfiguration() {
        this.acceptClientsConnections = false;
        this.ip = "0.0.0.0";
        this.port = 0;
        this.codecs = new LinkedList<>();
    }

    public NetworkConfiguration(int port) {
        this.acceptClientsConnections = true;
        this.ip = "0.0.0.0";
        this.port = port;
        this.codecs = new LinkedList<>();
    }

    public NetworkConfiguration(int port, String path) {
        this.acceptClientsConnections = true;
        this.ip = "0.0.0.0";
        this.port = port;
        this.path = path;
        this.codecs = new LinkedList<>();
    }

    public NetworkConfiguration path(String path) {
        this.path = path;
        return this;
    }

    public NetworkConfiguration networkDriver(Class<? extends INetworkDriver> driverClass) {
        this.driverClass = driverClass;
        return this;
    }

    public NetworkConfiguration maxTimeOuts(int maxTimeOuts) {
        this.maxTimeOuts = maxTimeOuts;
        return this;
    }

    public NetworkConfiguration iddleTimeInSeconds(int iddleTimeInSeconds) {
        this.iddleTimeInSeconds = iddleTimeInSeconds;
        return this;
    }

    public NetworkConfiguration ip(String ip) {
        this.ip = ip;
        return this;
    }

    public NetworkConfiguration sslContext(SslContext context) {
        this.sslContext = context;
        return this;
    }

    public NetworkConfiguration addCodec(IEventMessageCodec codec) {
        this.codecs.add(codec);
        return this;
    }

    public NetworkConfiguration removeCodec(IEventMessageCodec codec) {
        this.codecs.remove(codec);
        return this;
    }

    public int getPort() {
        return port;
    }

    public int getIddleTimeInSeconds() {
        return iddleTimeInSeconds;
    }

    public String getIp() {
        return ip;
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public List<IEventMessageCodec> getCodecs() {
        return codecs;
    }

    public Class<? extends INetworkDriver> getDriverClass() {
        return driverClass;
    }

    public int getMaxTimeOuts() {
        return maxTimeOuts;
    }

    public boolean isAcceptClients() {
        return acceptClientsConnections;
    }

    public String getPath() {
        return path;
    }

    public NetworkConfiguration acceptClients(boolean acceptClientsConnections) {
        this.acceptClientsConnections = acceptClientsConnections;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NetworkConfiguration [acceptClientsConnections=");
        builder.append(acceptClientsConnections);
        builder.append(", port=");
        builder.append(port);
        builder.append(", ip=");
        builder.append(ip);
        builder.append(", path=");
        builder.append(path);
        builder.append(", sslContext=");
        builder.append(sslContext);
        builder.append(", codecs=");
        builder.append(codecs);
        builder.append(", driverClass=");
        builder.append(driverClass);
        builder.append(", maxTimeOuts=");
        builder.append(maxTimeOuts);
        builder.append(", iddleTimeInSeconds=");
        builder.append(iddleTimeInSeconds);
        builder.append("]");
        return builder.toString();
    }
}
