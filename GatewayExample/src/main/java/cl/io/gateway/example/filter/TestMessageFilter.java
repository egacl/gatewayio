package cl.io.gateway.example.filter;

import cl.io.gateway.IGatewayClientSession;
import cl.io.gateway.example.networkservice.TestMessage;
import cl.io.gateway.messaging.GatewayMessageFilter;
import cl.io.gateway.messaging.IGatewayMessageFilter;
import cl.io.gateway.network.NetworkMessage;

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
