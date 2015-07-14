/* 
 * StompInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller.interceptor;

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

import edu.tamu.app.ApplicationContextProvider;
import edu.tamu.app.model.Credentials;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.WebSocketRequest;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.app.util.WebSocketRequestUtility;
import edu.tamu.app.util.jwt.JwtService;
import edu.tamu.framework.model.APIres;

/**
 * Stomp interceptor. Checks command, decodes and verifies token, 
 * either returns error message to frontend or continues to controller.
 * 
 * @author 
 *
 */
@Component
public class StompInterceptor extends ChannelInterceptorAdapter {
	
	@Value("${app.authority.admins}")
	private String[] admins;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private WebSocketRequestUtility webSocketRequestUtility;
	
	@Autowired
	private SecurityContext securityContext;
	
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
	    		((SimpMessagingTemplate) ApplicationContextProvider.appContext.getBean("brokerMessagingTemplate")).convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new APIres("failure", error, new RequestId(requestId)));
	    		return null;
	    	}
	    	
	    	if(jwtService.isExpired(credentialMap)) {
				System.out.println("Token expired!!!");	
				((SimpMessagingTemplate) ApplicationContextProvider.appContext.getBean("brokerMessagingTemplate")).convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new APIres("refresh", "EXPIRED_JWT", new RequestId(requestId)));
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
			
			webSocketRequestUtility.addRequest(new WebSocketRequest(newMessage, accessor.getDestination(), securityContext.getAuthentication().getName()));
			
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
		    		
		    		UserImpl user = userRepo.getUserByUin(Long.parseLong(shib.getUin()));
		    		
		    		if(user == null) {
			    		
			    		if(shib.getRole() == null) {
			    			shib.setRole("ROLE_USER");
			    		}
			        	String shibUin = shib.getUin();
			    		for(String uin : admins) {
			    			if(uin.equals(shibUin)) {
			    				shib.setRole("ROLE_ADMIN");					
			    			}
			    		}
			    		
			    		UserImpl newUser = new UserImpl();
			    		
			    		newUser.setUin(Long.parseLong(shib.getUin()));					
			    		newUser.setRole(shib.getRole());
			    		
			    		userRepo.save(newUser);
			        	
			        	System.out.println(shib.getFirstName() + " " + shib.getLastName() + " connected with session id " + headers.get("simpSessionId"));
			    		
			    		System.out.println(Long.parseLong(shib.getUin()));	
			    
			    	}
			    	else {
			    		shib.setRole(user.getRole());
			    	}
		    		
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
	
}
