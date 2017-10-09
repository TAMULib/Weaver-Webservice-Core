/* 
 * CoreWebConfigSupport.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import edu.tamu.framework.mapping.RestRequestMappingHandler;
import edu.tamu.framework.mapping.WebSocketRequestMappingHandler;

/**
 * Core Web MVC configuration support.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public abstract class CoreWebConfigSupport extends WebMvcConfigurationSupport {

    @Autowired
    private SubscribableChannel clientInboundChannel;

    @Autowired
    private MessageChannel clientOutboundChannel;

    @Autowired
    private SimpMessageSendingOperations brokerTemplate;

    @Autowired
    private ContentNegotiationManager contentNegotiationManager;

    /**
     * Rest request mapping handler bean.
     * 
     * @return RestRequestMappingHandler
     */
    @Bean
    public RestRequestMappingHandler restRequestMappingHandler() {
        RestRequestMappingHandler handlerMapping = new RestRequestMappingHandler(contentNegotiationManager);
        handlerMapping.setOrder(1);
        handlerMapping.setInterceptors(new Object[] { getRestInterceptor() });
        return handlerMapping;
    }

    public abstract Object getRestInterceptor();

    /**
     * Websocket request mapping handler bean
     * 
     * @return WebSocketRequestMappingHandler
     */
    @Bean
    public WebSocketRequestMappingHandler webSocketRequestMappingHandler() {
        return new WebSocketRequestMappingHandler(clientInboundChannel, clientOutboundChannel, brokerTemplate);
    }

}