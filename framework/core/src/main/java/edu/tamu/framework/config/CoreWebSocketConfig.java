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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/** 
 * Web Socket Configuration.
 * 
 * @author
 *
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public abstract class CoreWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer implements SchedulingConfigurer {
	
	 @Bean
	 public ThreadPoolTaskScheduler reservationPool() {
		 return new ThreadPoolTaskScheduler();
	 }
	
	/**
	 * Configure message broker. Enables simple broker queue and channel. 
	 * Sets prefix ws and user destination prefix private
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
	
	/**
	 * Configure websocket transport registration.
	 * 
	 * @param       registration    	WebSocketTransportRegistration
	 * 
	 */
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.setSendBufferSizeLimit(2 * 512 * 1024);
		registration.setSendTimeLimit(2 * 10 * 10000);
	}
	
	@Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.taskExecutor().corePoolSize(8).maxPoolSize(Integer.MAX_VALUE);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(8).maxPoolSize(Integer.MAX_VALUE);
    }
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(reservationPool());
    }
	
}

