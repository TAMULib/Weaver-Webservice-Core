/* 
 * WebSocketConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/** 
 * Web Socket Configuration.
 * 
 * @author
 *
 */
@Configuration
@EnableWebSocketMessageBroker
@Component
public abstract class CoreWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	/**
	 * Configure message broker. Enables simple broker queue and channel. 
	 * Sets prefix ws and user destination prefix privat.
	 * 
	 * @param       registry    	MessageBrokerRegistry
	 * 
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/queue", "/channel");
		registry.setApplicationDestinationPrefixes("/ws");
		registry.setUserDestinationPrefix("/private");
	}
	
	/**
	 * Register Stomp endpoints connect, user, and broadcast.
	 * 
	 * @param       registry    	StompEndpointRegistry
	 * 
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/connect").withSockJS();
		registry.addEndpoint("/user").withSockJS();
		registry.addEndpoint("/admin").withSockJS();
	}
	
}

