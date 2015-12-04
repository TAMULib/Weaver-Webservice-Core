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
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/** 
 * Web Socket Configuration.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public abstract class CoreWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	/**
	 * Configure message broker prefixes. Enables simple broker queue and channel. 
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
		registry.addEndpoint("/connect").setAllowedOrigins("*").withSockJS();
	}
	
}

