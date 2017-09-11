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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.network.NetworkEventType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Abstract class for communication betweeen network driver and netty channel
 * inbound handler.
 *
 * @author egacl
 * @param <T>
 *            Network message type.
 */
public abstract class AbstractDriverChannelInboundHandler<T> extends SimpleChannelInboundHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDriverChannelInboundHandler.class);

    private final AbstractNetworkDriver networkDriver;

    public AbstractDriverChannelInboundHandler(final AbstractNetworkDriver networkDriver) {
        this.networkDriver = networkDriver;
    }

    public AbstractNetworkDriver getNetworkDriver() {
        return networkDriver;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info("Channel " + this.networkDriver.getChannelId(ctx.channel()) + " " + NetworkEventType.REGISTERED);
        this.networkDriver.onNetworkEvent(this.networkDriver.getChannelId(ctx.channel()), ctx.channel(),
                NetworkEventType.REGISTERED);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        logger.info("Channel " + this.networkDriver.getChannelId(ctx.channel()) + " " + NetworkEventType.UNREGISTERED);
        this.networkDriver.onNetworkEvent(this.networkDriver.getChannelId(ctx.channel()), ctx.channel(),
                NetworkEventType.UNREGISTERED);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Channel " + this.networkDriver.getChannelId(ctx.channel()) + " " + NetworkEventType.ACTIVE);
        this.networkDriver.onNetworkEvent(this.networkDriver.getChannelId(ctx.channel()), ctx.channel(),
                NetworkEventType.ACTIVE);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("Channel " + this.networkDriver.getChannelId(ctx.channel()) + " " + NetworkEventType.INACTIVE);
        this.networkDriver.onNetworkEvent(this.networkDriver.getChannelId(ctx.channel()), ctx.channel(),
                NetworkEventType.INACTIVE);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            // Timeout event
            final String channelId = this.networkDriver.getChannelId(ctx.channel());
            logger.trace("Idle state event received for channel: " + channelId);
            final DriverChannel driverChannel = this.networkDriver.getDriverChannel(channelId);
            final int iddleStateCounter = driverChannel.addAndGetReconnectCounter();
            if (iddleStateCounter > this.networkDriver.getConfiguration().getMaxTimeOuts()) {
                throw new IOException("Timeouts limit exceded");
            }
            System.out.println("Se envia ping a channel " + this.networkDriver.getChannelId(ctx.channel()));
            this.sendPing(ctx.channel());
            logger.info("Channel " + this.networkDriver.getChannelId(ctx.channel()) + " "
                    + NetworkEventType.TIMEOUT_ALERT_ON);
            this.networkDriver.onNetworkEvent(this.networkDriver.getChannelId(ctx.channel()), ctx.channel(),
                    NetworkEventType.TIMEOUT_ALERT_ON);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        logger.error("Exception in channel: " + this.networkDriver.getChannelId(ctx.channel()), cause);
        ctx.close();
    }

    public abstract void sendPing(Channel channel);
}
