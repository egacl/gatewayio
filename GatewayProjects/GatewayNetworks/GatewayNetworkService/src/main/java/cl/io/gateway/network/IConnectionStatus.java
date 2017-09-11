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
 * Asynchronous connection status listener.
 *
 * @author egacl
 */
public interface IConnectionStatus {

    /**
     * This method is called when the connection to the channel is successfully
     * established.
     */
    void success(String channelId);

    /**
     * This method is called when it is not possible to establish the connection
     * with the channel.
     *
     * @param err
     *            Exception with the reason of error
     */
    void error(String channelId, Throwable err);
}
