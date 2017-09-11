package cl.io.gateway.example.networkservice;

import cl.io.gateway.network.INetworkService;
import cl.io.gateway.network.NetworkConfiguration;
import cl.io.gateway.network.NetworkConnection;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.NetworkService;
import cl.io.gateway.network.codec.GSonMessageCodec;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.websocketdriver.WebSocketDriver;

/**
 * Created by egacl on 26-07-16.
 */
public class MainClient {

    private static final Object THREAD_LOCK = new Object();

    private static final String TEST_EVENT = "testevent";

    public static void main(String[] args) throws Exception {
        //Initialice just client network configuration and set messages codec and network driver
        NetworkConfiguration netConf = new NetworkConfiguration()
        		.addCodec(new GSonMessageCodec())
        		.networkDriver(WebSocketDriver.class);
        //Initialice network messaging service
        final INetworkService networkService = new NetworkService(netConf);
        //Add network connection event handler
        networkService.addNetworkEventHandler((NetworkEvent event) -> {
            if(event.getEventType()==NetworkEventType.ACTIVE) {
                //When connection is active then send a message
                try {
                    System.out.println("Sending message to: " + event.getChannelId());
                    //Send a network message associated to "test" event
                    networkService.send(event.getChannelId(), new NetworkMessage<>(TEST_EVENT, new TestMessage("test message")));
                }
                catch (NetworkDriverException err) {
                    err.printStackTrace();
                }
            }
        });
        //Add network message handler to process "testevent" event
        networkService.addMessageHandler(TEST_EVENT, (NetworkMessage<TestMessage> message) -> {
            //Process "testevent" network event messsage
//            System.out.println("Receive response number '" + message.getChannelMessageSequence() + 
//            		"' from '" + message.getOriginChannelId() + "' server: " + message);
//            try {
//                networkService.send(message.getOriginChannelId(), message);
//            }
//            catch(DriverNetworkException err) {
//                err.printStackTrace();
//            }
        });
        //Try to connect to any websocket server
        networkService.start().connectTo(new NetworkConnection("myServerId", "ws://localhost:7010/ws"));
        //Block main thread
        synchronized (THREAD_LOCK) {
            try {
                THREAD_LOCK.wait();
            } catch (final InterruptedException e) {}
        }
    }

}
