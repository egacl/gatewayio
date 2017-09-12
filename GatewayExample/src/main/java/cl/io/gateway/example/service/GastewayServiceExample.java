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
import cl.io.gateway.example.networkservice.TestMessage;
import cl.io.gateway.messaging.GatewayMessageContext;
import cl.io.gateway.messaging.GatewayMessageHandler;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
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
@GatewayService("MyTestService")
public class GastewayServiceExample implements IGatewayService {

    /**
     * Gateway instance
     */
    private IGateway gateway;

    @Override
    public void initialize(final IGateway gateway) {
        System.out.println("Hello world! I'm a gateway service!");
        this.gateway = gateway;
    }

    /**
     * The @GatewayMessageHandler annotation allows to declare, in gateway services,
     * methods for processing different network messages from the unique event
     * identifier.
     *
     * @param messageContext
     *            Message and session that sent the message
     * @throws NetworkDriverException
     *             if a network error occurs
     */
    @GatewayMessageHandler(value = "testevent")
    public void processMyEvent(GatewayMessageContext<TestMessage> messageContext) throws NetworkDriverException {
        System.out.println("This message has been received!: " + messageContext.getMessage());
        // message echo
        messageContext.getClientSession().send(messageContext.getMessage());
        // Another way to do message echo
        // this.gateway.sendMessage(messageContext.getClientSession(),
        // messageContext.getMessage(),
        // messageContext.getClientSession().getOrigin());
    }
}
