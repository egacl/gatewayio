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
import cl.io.gateway.messaging.GatewayMessageFilter;
import cl.io.gateway.messaging.IGatewayMessageFilter;
import cl.io.gateway.network.NetworkMessage;

/**
 * This class represents an example message filter which is defined from
 * the @GatewayMessageFilter annotation and the IGatewayMessageFilter interface.
 * In this case, the filter applies to the event with the identifier 'testvent'
 * and priority '1'.
 * 
 * @author egacl
 *
 */
@GatewayMessageFilter(value = 1, event = "testevent", messageType = TestMessage.class)
public class TestMessageFilter implements IGatewayMessageFilter<TestMessage> {

    @Override
    public boolean doFilterRequest(NetworkMessage<TestMessage> message, IGatewayClientSession client) throws Exception {
        System.out.println("Hello! I'am a filter request priority 1: " + message);
        return true;
    }

    @Override
    public boolean doFilterResponse(NetworkMessage<TestMessage> message, IGatewayClientSession client)
            throws Exception {
        System.out.println("Hello! I'am a filter response priority 1: " + message);
        return true;
    }

    @Override
    public void onError(NetworkMessage<TestMessage> message, IGatewayClientSession client, Throwable err)
            throws Exception {
        System.err.println("Error processing " + message);
    }
}
