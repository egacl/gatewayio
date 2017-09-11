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
package cl.io.gateway.websocketdriver;

import java.net.URI;

import cl.io.gateway.network.IServiceDriverCommunication;
import cl.io.gateway.network.NetworkConfiguration;
import cl.io.gateway.network.driver.AbstractNetworkDriver;
import cl.io.gateway.network.driver.DriverClientNetworkConnection;
import cl.io.gateway.network.driver.IClientChannelInitializer;
import cl.io.gateway.network.driver.IDriverChannelOutboundHandler;
import cl.io.gateway.network.driver.INetworkDriverClientManager;
import cl.io.gateway.network.driver.INetworkDriverServer;
import cl.io.gateway.network.driver.SimpleNetworkDriverClientManager;
import cl.io.gateway.network.driver.SimpleNetworkDriverServer;
import cl.io.gateway.network.handler.NetworkUrl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Implementing Network Driver for the Websocket Protocol.
 *
 * @see AbstractNetworkDriver
 * @author egacl
 */
public class WebSocketDriver extends AbstractNetworkDriver {

    public WebSocketDriver(final NetworkConfiguration configuration,
            final IServiceDriverCommunication IServiceDriverCommunication, final EventLoopGroup bossGroup,
            final EventLoopGroup workerGroup) {
        super(configuration, IServiceDriverCommunication, bossGroup, workerGroup);
    }

    @Override
    public INetworkDriverServer createServer() {
        final int iddleTime = WebSocketDriver.this.getConfiguration().getIddleTimeInSeconds();
        return new SimpleNetworkDriverServer(WebSocketDriver.this, new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(final Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                pipeline.addLast(new ChunkedWriteHandler());
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new HttpRequestHandler(WebSocketDriver.this.getConfiguration().getPath()));
                pipeline.addLast(new TuningWebsocketServerProtocolHandler(WebSocketDriver.this,
                        WebSocketDriver.this.getConfiguration().getPath(), null, true));
                pipeline.addLast(new IdleStateHandler(iddleTime, iddleTime, iddleTime));
                pipeline.addLast(new ServerTextWebSocketFrameHandler(WebSocketDriver.this));
            }
        });
    }

    @Override
    public INetworkDriverClientManager createClient() {
        return new SimpleNetworkDriverClientManager(this, new IClientChannelInitializer<Channel>() {

            @Override
            public Bootstrap createBootstrapClientConnection(final DriverClientNetworkConnection networkConnection)
                    throws Exception {
                final NetworkUrl networkUrl = networkConnection.getUrlToConnect();
                final Bootstrap boostrap = new Bootstrap();
                final int iddleTime = WebSocketDriver.this.getConfiguration().getIddleTimeInSeconds();
                boostrap.group(WebSocketDriver.this.getWorkerGroup()).channel(NioSocketChannel.class);
                final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                        validateURI(networkUrl), WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
                final ClientTextWebSocketFrameHandler handler = new ClientTextWebSocketFrameHandler(
                        WebSocketDriver.this, networkConnection.getChannelId(), networkConnection);
                boostrap.handler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                        pipeline.addLast(new TuningWebSocketClientProtocolHandler(WebSocketDriver.this, handshaker));
                        pipeline.addLast(new WebSocketFrameAggregator(16 * 1024 * 1024));
                        pipeline.addLast(new IdleStateHandler(iddleTime, iddleTime, iddleTime));
                        pipeline.addLast(handler);
                    }
                });
                return boostrap;
            }
        });
    }

    /**
     * URL validation.
     *
     * @param networkUrl
     *            url connection data
     * @return URI instance
     */
    static URI validateURI(final NetworkUrl networkUrl) {
        // Validate the URI scheme
        final String scheme = networkUrl.getUri().getScheme();
        if (!"ws".equals(scheme) && !"wss".equals(scheme)) {
            throw new IllegalArgumentException("Invalid URI scheme: " + networkUrl.getUrl());
        }
        // Validate the URI host
        final String host = networkUrl.getUri().getHost();
        if (host == null) {
            throw new IllegalArgumentException("Invalid host specified: " + networkUrl.getUrl());
        }
        return networkUrl.getUri();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IDriverChannelOutboundHandler createOutBoundHandler() {
        return new TextWebSocketOutboundHandler(this, String.class);
    }

    @Override
    public String getChannelId(final Channel channel) {
        if (channel.remoteAddress() != null) {
            return channel.remoteAddress().toString();
        }
        return null;
    }
}
