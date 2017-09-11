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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.driver.AbstractNetworkDriver;
import cl.io.gateway.network.driver.DriverChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * This class allows you to capture ping and pong messages from the connections
 * to other network servers.
 *
 * @author egacl
 *
 */
public class TuningWebSocketClientProtocolHandler extends WebSocketClientProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(TuningWebSocketClientProtocolHandler.class);

    private final AbstractNetworkDriver networkDriver;

    public TuningWebSocketClientProtocolHandler(final AbstractNetworkDriver networkDriver,
            WebSocketClientHandshaker handshaker) {
        super(handshaker);
        this.networkDriver = networkDriver;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (frame instanceof PongWebSocketFrame) {
            this.pongReceived(ctx);
        }
        super.decode(ctx, frame, out);
    }

    public void pongReceived(final ChannelHandlerContext ctx) {
        // Se procesa evento de hearbeat
        final String channelId = this.getChannelId(ctx.channel());
        logger.trace(channelId + ": Ping response received");
        // Se resetea el contador de timeout
        final DriverChannel driverChannel = this.networkDriver.getDriverChannel(channelId);
        if (driverChannel != null) {
            driverChannel.resetReconnectCounter();
            logger.info("Channel " + this.getChannelId(ctx.channel()) + " " + NetworkEventType.TIMEOUT_ALERT_OFF);
            this.networkDriver.onNetworkEvent(this.getChannelId(ctx.channel()), ctx.channel(),
                    NetworkEventType.TIMEOUT_ALERT_OFF);
        }
    }

    public String getChannelId(final Channel channel) {
        return channel.remoteAddress().toString();
    }
}
