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
 * Adapter pattern for asynchronous connection status listener
 * {@link IConnectionStatus}.
 *
 * @author egacl
 */
public abstract class ConnectionStatusAdaper implements IConnectionStatus {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionStatusAdaper.class);

    @Override
    public void success(String channelId) {
        logger.info("Established connection with channel: " + channelId);
    }

    @Override
    public void error(String channelId, Throwable err) {
        logger.error("Error connecting channel: " + channelId, err);
    }
}
