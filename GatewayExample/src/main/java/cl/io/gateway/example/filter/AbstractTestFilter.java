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
package cl.io.gateway.example.filter;

import cl.io.gateway.IGatewayClientSession;
import cl.io.gateway.example.networkservice.TestMessage;
import cl.io.gateway.messaging.IGatewayMessageFilter;
import cl.io.gateway.network.NetworkMessage;

/**
 * Example filter abstract class.
 *
 * @author egacl
 *
 */
public abstract class AbstractTestFilter implements IGatewayMessageFilter<TestMessage> {

    @Override
    public void onError(NetworkMessage<TestMessage> message, IGatewayClientSession client, Throwable err)
            throws Exception {
        System.err.println("Error processing 2" + message);
    }
}
