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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.driver.AbstractNetworkDriver;
import cl.io.gateway.network.driver.DriverClientNetworkConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

/**
 * A class that allows you to receive network events from the connections that
 * the network driver makes to other servers. Communicates with client
 * connection manager.
 *
 * @see AbstractTextWebSocketFrameHandler
 * @see DriverClientNetworkConnection
 * @author egacl
 */
public class ClientTextWebSocketFrameHandler extends AbstractTextWebSocketFrameHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientTextWebSocketFrameHandler.class);

    private final String channelId;

    private final DriverClientNetworkConnection connection;

    public ClientTextWebSocketFrameHandler(final AbstractNetworkDriver networkDriver, final String channelId,
            final DriverClientNetworkConnection connection) {
        super(networkDriver);
        this.channelId = channelId;
        this.connection = connection;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        // Call client manager for reconnect
        this.getNetworkDriver().getClientManager().scheduledReconnect(this.connection);
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            logger.info(
                    "Channel " + this.getNetworkDriver().getChannelId(ctx.channel()) + " " + NetworkEventType.ACTIVE);
            this.getNetworkDriver().onNetworkEvent(this.getNetworkDriver().getChannelId(ctx.channel()), ctx.channel(),
                    NetworkEventType.ACTIVE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public String getChannelId() {
        return channelId;
    }
}
