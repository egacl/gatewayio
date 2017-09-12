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
package cl.io.gateway.example.service;

import cl.io.gateway.IGateway;
import cl.io.gateway.IGatewayClientSession;
import cl.io.gateway.example.networkservice.TestMessage;
import cl.io.gateway.exception.GatewayProcessException;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.service.GatewayService;
import cl.io.gateway.service.IGatewayService;

/**
 * Example gateway service class. You must use the @GatewayService annotation
 * and the IGatewayService interface so that the framework initializes the
 * service.
 * 
 * @author egacl
 *
 */
@GatewayService("NoAnnotatedHandler")
public class NoAnnotationGatewayService implements IGatewayService {

    @Override
    public void initialize(final IGateway gateway) {
        try {
            // Add message handler for 'testevent2' event id
            gateway.addMessageHandler("testevent2",
                    (NetworkMessage<TestMessage> message, IGatewayClientSession session) -> {
                        System.out.println("This message has been received!: " + message + " from " + session);
                        // message echo
                        session.send(message);
                        // Another way to do message echo
                        // gateway.sendMessage(session, message, session.getOrigin());
                    });
        } catch (GatewayProcessException e) {
            System.err.println(e);
        }
    }
}
