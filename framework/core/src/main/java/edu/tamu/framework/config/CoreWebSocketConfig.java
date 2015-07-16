/* 
 * WebSocketConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import edu.tamu.app.controller.interceptor.StompInterceptor;

/** 
 * Web Socket Configuration.
 * 
 * @author
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	@Autowired
	public StompInterceptor stompInterceptor;
	
	/**
	 * Stomp interceptor bean.
	 * 
	 * @return		StompInterceptor
	 * 
	 */
	@Bean
	public StompInterceptor configureStompInterceptor() {
		return new StompInterceptor();
	}
	
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
	
	/**
	 * Configure client inbound channel. Sets stomp interceptor. 
	 * 
	 * @param       registration   	ChannelRegistration
	 * 
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(stompInterceptor);
	}

}

