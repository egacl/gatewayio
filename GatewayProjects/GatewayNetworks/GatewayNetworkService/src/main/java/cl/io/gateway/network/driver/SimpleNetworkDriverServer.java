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

import cl.io.gateway.network.NetworkConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Initializes and manages the server entity for receiving network connections.
 *
 * @author egacl
 */
public class SimpleNetworkDriverServer implements INetworkDriverServer {

    /**
     * Server boostrap instance
     */
    private final ServerBootstrap bootstrap;

    /**
     * Server channel instance
     */
    private ChannelFuture channelFuture;

    /**
     * Network configuration data
     */
    private final NetworkConfiguration configuration;

    public SimpleNetworkDriverServer(final AbstractNetworkDriver networkDriver,
            final ChannelInitializer<Channel> initializer) {
        this.configuration = networkDriver.getConfiguration();
        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(networkDriver.getBossGroup(), networkDriver.getWorkerGroup())
                .channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                // Call network driver implementation initializer
                .childHandler(initializer);
    }

    @Override
    public void start() {
        if (this.channelFuture == null) {
            this.channelFuture = this.bootstrap.bind(this.configuration.getIp(), this.configuration.getPort());
            this.channelFuture.syncUninterruptibly();
        }
    }

    @Override
    public void stop() {
        if (this.channelFuture != null) {
            this.channelFuture.channel().close().syncUninterruptibly();
        }
    }

    @Override
    public void initialice() {
        // no need to implement
    }
}
