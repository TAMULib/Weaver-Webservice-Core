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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
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
import edu.tamu.framework.model.RequestId;
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
	
	@Value("${app.authority.admins}")
	String[] admins;
	
	@Autowired
	private JwtUtility jwtService;
	
	@Autowired
	private WebSocketRequestService webSocketRequestService;
	
	@Autowired
	private SecurityContext securityContext;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
	private List<String> currentUsers = new ArrayList<String>();
	
	
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
	@SuppressWarnings("unchecked")
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
				
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
		
		if(accessor.getDestination() != null) {
		  System.out.println(accessor.getDestination());
		}
		
		System.out.println(command.name());
		
		if("SEND".equals(command.name())) {
			
			String requestId = accessor.getNativeHeader("id").get(0);
			
			MessageHeaders headers = message.getHeaders();	
			Map<String, Object> headerMap = (Map<String, Object>) headers.get("nativeHeaders");		
			
			String jwt = headerMap.get("jwt").toString();			
			jwt = jwt.substring(1, jwt.length()-1);
			
			Map<String, String> credentialMap = jwtService.validateJWT(jwt);
			
			String error = credentialMap.get("ERROR"); 
	    	if(error != null) {
	    		
	    		
	    		System.out.println("\n" + securityContext.getAuthentication().getName() + "\n");
	    		
	    		System.out.println("JWT error: " + error);
	    		simpMessagingTemplate.convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResponse("failure", error, new RequestId(requestId)));
	    		return null;
	    	}
	    	
	    	if(jwtService.isExpired(credentialMap)) {
				System.out.println("Token expired!!!");	
				simpMessagingTemplate.convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResponse("refresh", "EXPIRED_JWT", new RequestId(requestId)));
				return null;		
			}
			
			
			Credentials shib = new Credentials(credentialMap);
			String shibUin = shib.getUin();
			for(String uin : admins) {
				if(uin.equals(shibUin)) {
					shib.setRole("ROLE_ADMIN");					
				}
			}
			
						
			Map<String, Object> shibMap = new HashMap<String, Object>();
			
			shibMap.put("shib", shib);
			
			accessor.setSessionAttributes(shibMap);
			
			Message<?> newMessage = MessageBuilder.withPayload("VALID_JWT").setHeaders(accessor).build();
			
			webSocketRequestService.addRequest(new WebSocketRequest(newMessage, accessor.getDestination(), securityContext.getAuthentication().getName()));
			
			return newMessage;
			
		}
		else if("CONNECT".equals(command.name())) {
			
			MessageHeaders headers = message.getHeaders();
		    Map<String, Object> headerMap = (Map<String, Object>) headers.get("nativeHeaders");
		    String jwt = headerMap.get("jwt").toString();
		    
		    if(!"[undefined]".equals(jwt)) {
		    	
		    	jwt = jwt.substring(1, jwt.length()-1);
		    	
		    	Map<String, String> credentialMap = jwtService.validateJWT(jwt);
		    	
		    	String error = credentialMap.get("ERROR"); 
		    	if(error != null) {
		    		System.err.println("Unknown error: " + error);
		    		return MessageBuilder.withPayload(error).setHeaders(accessor).build();
		    	}
		    	
		    	Credentials shib = new Credentials(credentialMap);
		    	
		    	if(!("ROLE_ANONYMOUS").equals(shib.getRole())) {
		    		
		    		shib = confirmCreateUser(shib);
		    		
		    	}
		    	
		    	
				List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
				
				grantedAuthorities.add(new SimpleGrantedAuthority(shib.getRole()));
				
				if(("ROLE_ANONYMOUS").equals(shib.getRole())) {
					shib.setNetid(shib.getNetid() + "-" + currentUsers.size());					
				}
				
				currentUsers.add(shib.getNetid());
				
				Authentication auth = new AnonymousAuthenticationToken(shib.getUin(), shib.getNetid(), grantedAuthorities);
				
				auth.setAuthenticated(true);
				
				securityContext.setAuthentication(auth);
				
		    }
		    
		}
		else if("DISCONNECT".equals(command.name())) {
			
			currentUsers.remove(securityContext.getAuthentication().getName());
			
			System.out.println(currentUsers.size() + " users with websocket connections.");
			
			System.out.println(securityContext.getAuthentication().getName() + ", you're web socket connection finished.");
						
		}
		else if("SUBSCRIBE".equals(command.name())) {
			System.out.println("Subscribing.");
		}
		else if("UNSUBSCRIBE".equals(command.name())) {
			System.out.println("Unsubscribing.");
		}
		
		return message;
	}
	
	public abstract Credentials confirmCreateUser(Credentials shib);

}
