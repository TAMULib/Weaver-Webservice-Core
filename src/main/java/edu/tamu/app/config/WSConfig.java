package edu.tamu.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * WebSocket configuration.
 * 
 * @author
 */
@Configuration
@EnableWebSocketMessageBroker
public class WSConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	/**
	 * Configure message broker.
	 * 
	 * @param config
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/channel");
		config.setApplicationDestinationPrefixes("/ws");
	}
	
	/**
	 * Register stomp endpoint.
	 * 
	 * @param registry
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/user").withSockJS();
	}

}

