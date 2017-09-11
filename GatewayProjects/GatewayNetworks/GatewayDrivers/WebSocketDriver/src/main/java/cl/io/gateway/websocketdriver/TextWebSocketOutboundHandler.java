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
import cl.io.gateway.network.driver.AbstractDriverChannelOutboundHandler;
import cl.io.gateway.network.driver.AbstractNetworkDriver;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Class that allows serializing java messages to transform them into a
 * TextWebSocket message.
 * 
 * @author egacl
 */
public class TextWebSocketOutboundHandler extends AbstractDriverChannelOutboundHandler<TextWebSocketFrame, String> {

    public TextWebSocketOutboundHandler(AbstractNetworkDriver driver, Class<String> protocolClass) {
        super(driver, protocolClass);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public TextWebSocketFrame messageToSend(final NetworkMessage msg) {
        try {
            final String strJsonMsg = this.getDriver().serialize(this.getProtocolClass(), msg);
            return new TextWebSocketFrame(strJsonMsg);
        } catch (Throwable err) {
            err.printStackTrace();
        }
        // TODO temporal
        return new TextWebSocketFrame("{}");
    }
}
