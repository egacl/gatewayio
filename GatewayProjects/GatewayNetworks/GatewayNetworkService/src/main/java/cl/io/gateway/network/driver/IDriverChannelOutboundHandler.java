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

/**
 * Interface that allows you to create an instance for the processing of
 * messages that are sent over the network.
 *
 * @author egacl
 * @param <T>
 *            Object Type to send to the network channel.
 * @param <P>
 *            Object type to decode the network message.
 */
@SuppressWarnings("rawtypes")
public interface IDriverChannelOutboundHandler<T, P> {

    void write(final NetworkMessage msg, DriverChannel channel) throws Exception;

    void write(final NetworkMessage msg, DriverChannel channel, IDeliveryStatusListener deliveryStatus) throws Exception;

    T messageToSend(NetworkMessage msg);

    AbstractNetworkDriver getDriver();

    Class<P> getProtocolClass();
}
