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
package cl.io.gateway.network;

/**
 * Asynchronous message delivery status listener.
 */
public interface IDeliveryStatusListener {

    /**
     * This method is called when message is successfully sended to the channel.
     *
     * @param channelId
     *            channel to send message
     * @param message
     *            message to send
     */
    <T> void success(String channelId, NetworkMessage<T> message);

    /**
     * This method is called when it is not possible send message to the channel.
     *
     * @param channelId
     *            channel to send message
     * @param message
     *            message to send
     * @param cause
     *            Exception with the reason of error
     */
    <T> void error(String channelId, NetworkMessage<T> message, Throwable cause);
}
