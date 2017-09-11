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

import cl.io.gateway.network.driver.AbstractNetworkDriver;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * A class that allows you to receive network events from the connections that
 * connect to this server. Communicates directly with network driver.
 *
 * @see AbstractTextWebSocketFrameHandler
 * @author egacl
 */
@ChannelHandler.Sharable
public class ServerTextWebSocketFrameHandler extends AbstractTextWebSocketFrameHandler {

    public ServerTextWebSocketFrameHandler(AbstractNetworkDriver networkDriver) {
        super(networkDriver);
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HttpRequestHandler.class);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
