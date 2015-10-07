/* 
 * StompInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.interceptor;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.REFRESH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.WebSocketRequestService;
import edu.tamu.framework.util.JwtUtility;

/**
 * Stomp interceptor. Checks command, decodes and verifies token, 
 * either returns error message to frontend or continues to controller.
 * 
 * @author 
 *
 */
@Component
public abstract class CoreStompInterceptor extends ChannelInterceptorAdapter {
	
	@Autowired
	private JwtUtility jwtService;
	
	@Autowired
	private WebSocketRequestService webSocketRequestService;
	
	@Autowired
	private SecurityContext securityContext;
	
	@Autowired @Lazy
	private SimpMessagingTemplate simpMessagingTemplate;
	
	private static Credentials anonymousShib;
	
	private List<String> currentUsers = new ArrayList<String>();
	
	private static final Logger logger = Logger.getLogger(CoreStompInterceptor.class);
	
	public CoreStompInterceptor() {
		anonymousShib = new Credentials();
		anonymousShib.setAffiliation("NA");
		anonymousShib.setLastName("Anonymous");
		anonymousShib.setFirstName("Role");
		anonymousShib.setNetid("anonymous");
		anonymousShib.setUin("000000000");
		anonymousShib.setExp("1436982214754");
		anonymousShib.setEmail("helpdesk@library.tamu.edu");
		anonymousShib.setRole( "ROLE_ANONYMOUS");
	}
	
	/**
	 * Override method to perform preprocessing before sending message.
	 * 
	 * @param		message			Message<?>
	 * @param		channel			MessageChannel
	 * 
	 * @return		Message<?>
	 * 
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
		
		if(accessor.getDestination() != null) {
			logger.debug("Accessor Destination: " + accessor.getDestination());
		}
		
		logger.debug(command.name());
		
		String jwt = null;
		
		if(accessor.getNativeHeader("jwt") != null) {
			jwt = accessor.getNativeHeader("jwt").get(0);
		}
		
		if("SEND".equals(command.name())) {
			
			logger.debug("Sending.");
			
			String requestId = accessor.getNativeHeader("id").get(0);
			
			Credentials shib;
			
			if(jwt != null && !"undefined".equals(jwt)) {
				
				Map<String, String> credentialMap = new HashMap<String, String>();
			
				credentialMap = jwtService.validateJWT(jwt);
				
				logger.info(credentialMap.get("firstName") + " " + credentialMap.get("lastName") + " (" + credentialMap.get("uin") + ") requesting " + accessor.getDestination());
				
				if(logger.isDebugEnabled()) {
					logger.debug("Credential Map");
					for(String key : credentialMap.keySet()) {
						logger.debug(key+" - "+credentialMap.get(key));
					}
				}
				
				String errorMessage = credentialMap.get("ERROR"); 
		    	if(errorMessage != null) {
		    		logger.error("Security Context Name: " + securityContext.getAuthentication().getName());	    		
		    		logger.error("JWT error: " + errorMessage);
		    		simpMessagingTemplate.convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResponse(requestId, ERROR, errorMessage));
		    		return null;
		    	}
		    	
		    	if(jwtService.isExpired(credentialMap)) {
					logger.info("The token for "+credentialMap.get("firstName")+" "+credentialMap.get("lastName")+" ("+credentialMap.get("uin")+") has expired. Attempting to get new token.");
					simpMessagingTemplate.convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResponse(requestId, REFRESH));
					return null;		
				}
							
				shib = new Credentials(credentialMap);
				
		    	shib = confirmCreateUser(shib);

			}
			else {
				shib = anonymousShib;
			}
									
			Map<String, Object> shibMap = new HashMap<String, Object>();
			
			shibMap.put("shib", shib);
			
			accessor.setSessionAttributes(shibMap);
			
			Message<?> newMessage = MessageBuilder.withPayload("VALID").setHeaders(accessor).build();
						
			webSocketRequestService.addRequest(new WebSocketRequest(newMessage, accessor.getDestination(), securityContext.getAuthentication().getName()));
			
			return newMessage;			
		}
		else if("CONNECT".equals(command.name())) {
			logger.debug("Connecting.");
			
			Credentials shib;
					    
			if(jwt != null && !"undefined".equals(jwt)) {
		    	
		    	Map<String, String> credentialMap = jwtService.validateJWT(jwt);
		    	
		    	if(logger.isDebugEnabled()) {
			    	logger.debug("Credential Map");
					for(String key : credentialMap.keySet()) {
						logger.debug(key+" - "+credentialMap.get(key));
					}
		    	}
		    	
		    	String errorMessage = credentialMap.get("ERROR"); 
		    	if(errorMessage != null) {
		    		logger.error("JWT error: " + errorMessage);
		    		return MessageBuilder.withPayload(errorMessage).setHeaders(accessor).build();
		    	}
		    	
		    	shib = new Credentials(credentialMap);
		    	
	    		shib = confirmCreateUser(shib);
	    			
				currentUsers.add(shib.getNetid());
											
		    }
			else {
				shib = anonymousShib;
				shib.setNetid(shib.getNetid() + "-" + currentUsers.size());
			}
			
			List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
			
			grantedAuthorities.add(new SimpleGrantedAuthority(shib.getRole()));
			
			Authentication auth = new AnonymousAuthenticationToken(shib.getUin(), shib.getNetid(), grantedAuthorities);
			
			auth.setAuthenticated(true);
			
			securityContext.setAuthentication(auth);
		    
		}
		else if("DISCONNECT".equals(command.name())) {
			logger.debug("Disconnecting websocket connection for " + securityContext.getAuthentication().getName() + ".");
			currentUsers.remove(securityContext.getAuthentication().getName());
			logger.debug("There are now " + currentUsers.size() + " users with websocket connections.");
		}
		else if("SUBSCRIBE".equals(command.name())) {
			logger.debug("Subscribing.");
		}
		else if("UNSUBSCRIBE".equals(command.name())) {
			logger.debug("Unsubscribing.");
		}
		
		return message;
	}
	
	public abstract Credentials confirmCreateUser(Credentials shib);
	
}
