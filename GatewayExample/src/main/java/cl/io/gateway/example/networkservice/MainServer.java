/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.io.gateway.example.networkservice;

import cl.io.gateway.network.INetworkService;
import cl.io.gateway.network.NetworkConfiguration;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkMessage;
import cl.io.gateway.network.NetworkService;
import cl.io.gateway.network.codec.GSonMessageCodec;
import cl.io.gateway.network.driver.exception.NetworkDriverException;
import cl.io.gateway.websocketdriver.WebSocketDriver;

/**
 *
 * @author egacl
 */
public class MainServer {

    private static final Object THREAD_LOCK = new Object();

    private static final String TEST_EVENT = "testevent";

    public static void main(String[] args) throws Exception {
        //Initialice server network configuration and set messages codec and network driver, listening on 7800 port
        NetworkConfiguration netConf = new NetworkConfiguration(7800)
        		.path("/ws")
        		.addCodec(new GSonMessageCodec())
        		.networkDriver(WebSocketDriver.class);
        //Network messaging service initialization
        final INetworkService networkService = new NetworkService(netConf);
        networkService.addNetworkEventHandler((NetworkEvent event) -> {
            System.out.println("Evento de red: " + event);
        });
        //Add network message handler to process "testevent" event from any client
        networkService.addMessageHandler(TEST_EVENT, (NetworkMessage<TestMessage> clientMessage) -> {
            //Process "testevent" network event messsage received from any client
            System.out.println("Receive clientMessage number '" + clientMessage.getChannelMessageSequence() + 
            		"' from '" + clientMessage.getOriginChannelId() + "' client: " + clientMessage + 
            		", events counter: " + clientMessage.getEventMessageSequence());
            try {
                networkService.send(clientMessage.getOriginChannelId(), clientMessage);
            }
            catch(NetworkDriverException err) {
                err.printStackTrace();
            }
        });
        //Start network messaging service
        networkService.start();
        //Block main thread
        synchronized (THREAD_LOCK) {
            try {
                THREAD_LOCK.wait();
            } catch (final InterruptedException e) {}
        }
    }
}
