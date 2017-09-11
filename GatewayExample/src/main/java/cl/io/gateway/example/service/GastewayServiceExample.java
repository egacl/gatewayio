package cl.io.gateway.example.service;

import cl.io.gateway.IGateway;
import cl.io.gateway.example.networkservice.TestMessage;
import cl.io.gateway.messaging.GatewayMessageContext;
import cl.io.gateway.messaging.GatewayMessageHandler;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.service.GatewayService;
import cl.io.gateway.service.IGatewayService;

@GatewayService("MyTestService")
public class GastewayServiceExample implements IGatewayService {

	@Override
	public void initialize(final IGateway gateway) {
		System.out.println("Hola  Mundo, soy un servicio de Gateway");
	}
	
	@GatewayMessageHandler(value="testevent")
	public void processMyEvent(GatewayMessageContext<TestMessage> messageContext) throws NetworkDriverException {
		System.out.println("Se recibe el mensaje: " + messageContext.getMessage());
		//retorna el mismo mensaje
		messageContext.getClientSession().send(messageContext.getMessage());
	}
	
}
