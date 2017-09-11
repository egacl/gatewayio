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

import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.driver.AbstractDriverChannelInboundHandler;
import cl.io.gateway.network.driver.AbstractNetworkDriver;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * An abstract class that allows you to receive websocket messages from a
 * participating channel on the network (either client or server)
 *
 * @author egacl
 *
 */
public abstract class AbstractTextWebSocketFrameHandler extends AbstractDriverChannelInboundHandler<WebSocketFrame> {

    public AbstractTextWebSocketFrameHandler(AbstractNetworkDriver networkDriver) {
        super(networkDriver);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame) throws Exception {
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
        }
        // protocol message deserealization, textwebsocket to networkmessage
        NetworkMessage networkMessage = this.getNetworkDriver().deserealize(String.class,
                ((TextWebSocketFrame) frame).text());
        // delivers the desearilized message to the network driver
        this.getNetworkDriver().onNetworkMessage(this.getNetworkDriver().getChannelId(ctx.channel()), networkMessage);
    }

    @Override
    public void sendPing(Channel channel) {
        // send websocket ping
        channel.writeAndFlush(new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 })));
    }
}
