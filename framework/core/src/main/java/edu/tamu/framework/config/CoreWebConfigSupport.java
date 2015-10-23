package edu.tamu.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import edu.tamu.framework.mapping.RestRequestMappingHandler;
import edu.tamu.framework.mapping.WebSocketRequestMappingHandler;

@Configuration
public abstract class CoreWebConfigSupport extends WebMvcConfigurationSupport {
	
	@Autowired
	private SubscribableChannel clientInboundChannel;
	
	@Autowired
	private MessageChannel clientOutboundChannel;
	
	@Autowired
	private SimpMessageSendingOperations brokerTemplate;
	
	@Autowired
	private ContentNegotiationManager contentNegotiationManager;
	
	@Bean
	public RestRequestMappingHandler restRequestMappingHandler() {
		RestRequestMappingHandler handlerMapping = new RestRequestMappingHandler(contentNegotiationManager);
		handlerMapping.setInterceptors(new Object[] { getRestInterceptor() });
		return handlerMapping;
	}
	
	public abstract Object getRestInterceptor();
		
	@Bean
	public WebSocketRequestMappingHandler webSocketRequestMappingHandler() {
		WebSocketRequestMappingHandler handlerMapping = new WebSocketRequestMappingHandler(clientInboundChannel, clientOutboundChannel, brokerTemplate);
		return handlerMapping;
	}

}