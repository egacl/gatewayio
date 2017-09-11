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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter pattern for asynchronous message delivery status listener.
 */
public class DeliveryStatusAdapter implements IDeliveryStatusListener {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryStatusAdapter.class);

    @Override
    public <T> void success(String channelId, NetworkMessage<T> message) {
        logger.info("Message sent successfully '%s': %o", channelId, message);
    }

    @Override
    public <T> void error(String channelId, NetworkMessage<T> message, Throwable cause) {
        logger.error("Unsent message '" + channelId + "': " + message, cause);
    }
}
