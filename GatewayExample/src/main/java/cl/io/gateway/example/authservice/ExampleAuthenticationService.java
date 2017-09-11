package cl.io.gateway.example.authservice;

import cl.io.gateway.IGateway;
import cl.io.gateway.auth.AuthenticationService;
import cl.io.gateway.auth.AuthenticationStatus;
import cl.io.gateway.auth.IAuthenticationService;
import cl.io.gateway.auth.IAuthenticationGatewayNetworkService;
import cl.io.gateway.messaging.NetworkServiceSource;
import cl.io.gateway.network.NetworkEvent;
import cl.io.gateway.network.NetworkEventType;
import cl.io.gateway.network.handler.INetworkEventListener;
import cl.io.gateway.vo.GatewayClient;

@AuthenticationService(authProtocolEvents = { "LOGIN, LOGOUT" }, value = NetworkServiceSource.ADMIN)
public class ExampleAuthenticationService implements IAuthenticationService {

	@Override
	public void initialize(final IGateway gateway, final IAuthenticationGatewayNetworkService netService) throws Exception {
		System.out.println("Hello! I'm an authentication service!");
		netService.addNetworkEventHandler(new INetworkEventListener() {
			@Override
			public void onEvent(NetworkEvent event) {
				if(event.getEventType()==NetworkEventType.ACTIVE) {
					netService.clientAuthenticated(new GatewayClient(event.getChannelId()), AuthenticationStatus.LOGGED_IN);
				}
				else if(event.getEventType()==NetworkEventType.INACTIVE) {
					netService.clientAuthenticated(new GatewayClient(event.getChannelId()), AuthenticationStatus.LOGGED_OUT);
				}
			}
		});
	}

}
