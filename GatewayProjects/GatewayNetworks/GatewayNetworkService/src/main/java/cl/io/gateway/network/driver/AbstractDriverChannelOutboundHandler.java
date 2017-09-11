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

import cl.io.gateway.network.IDeliveryStatusListener;
import cl.io.gateway.network.NetworkMessage;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Abstract class for communication betweeen network driver and netty channel
 * outbound handler.
 *
 * @author egacl
 * @param <T>
 *            Object Type to send to the network channel.
 * @param <P>
 *            Object type to decode the network message.
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractDriverChannelOutboundHandler<T, P> implements IDriverChannelOutboundHandler<T, P> {

    private final AbstractNetworkDriver driver;

    private final Class<P> protocolClass;

    public AbstractDriverChannelOutboundHandler(final AbstractNetworkDriver driver, final Class<P> protocolClass) {
        this.driver = driver;
        this.protocolClass = protocolClass;
    }

    @Override
    public void write(final NetworkMessage msg, final DriverChannel channel) throws Exception {
        this.write(msg, channel, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(final NetworkMessage msg, final DriverChannel channel, final IDeliveryStatusListener deliveryStatus)
            throws Exception {
        final ChannelFuture channelFuture = channel.getChannel().writeAndFlush(this.messageToSend(msg));
        if (deliveryStatus != null) {
            channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {

                @Override
                public void operationComplete(final ChannelFuture future) throws Exception {
                    try {
                        if (future.isSuccess()) {
                            deliveryStatus.success(channel.getChannelId(), msg);
                        } else {
                            deliveryStatus.error(channel.getChannelId(), msg, future.cause());
                        }
                    } finally {
                        // se escribe estadistica de envio de mensaje
                        driver.countSendEvent(msg.getEvent(), future.isSuccess());
                    }
                }
            });
        }
    }

    @Override
    public AbstractNetworkDriver getDriver() {
        return driver;
    }

    @Override
    public Class<P> getProtocolClass() {
        return protocolClass;
    }
}
