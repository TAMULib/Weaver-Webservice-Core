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

import java.util.Calendar;
import java.util.HashMap;
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
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.ApplicationContextProvider;
import edu.tamu.app.model.Credentials;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.model.repo.UserRepo;

/**
 * Stomp interceptor. Checks command, decodes and verifies token, 
 * either returns error message to frontend or continues to controller.
 * 
 * @author 
 *
 */
@Component
public class StompInterceptor extends ChannelInterceptorAdapter {
	
	@Value("${app.security.jwt.secret_key}") 
	private String secret_key;
	
	@Value("${app.authority.admins}")
	private String[] admins;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	public ObjectMapper objectMapper;
	
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
		
		MacSigner hmac = new MacSigner(secret_key);
		
		if(accessor.getDestination() != null)
		  System.out.println(accessor.getDestination());
		
		System.out.println(command.name());
		
		if("SEND".equals(command.name())) {
			
			String requestId = accessor.getNativeHeader("id").get(0);
			
			MessageHeaders headers = message.getHeaders();	
			Map<String, Object> headerMap = (Map<String, Object>) headers.get("nativeHeaders");		
			
			String jwt = headerMap.get("jwt").toString();			
			jwt = jwt.substring(1, jwt.length()-1);
			
			if(jwt == null) {
				System.out.println("Missing token!");
				((SimpMessagingTemplate) ApplicationContextProvider.appContext.getBean("brokerMessagingTemplate")).convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResImpl("failure", "MISSING_JWT", new RequestId(requestId)));
				return null;
			}
			
			Jwt token = null;
			Map<String, String> tokenMap = null;
			try {
				token = JwtHelper.decodeAndVerify(jwt, hmac);				
				tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
			} catch (Exception e) {
				System.out.println("Invalid token!");
				((SimpMessagingTemplate) ApplicationContextProvider.appContext.getBean("brokerMessagingTemplate")).convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResImpl("failure", "INVALID_UIN", new RequestId(requestId)));
				return null;
			}
			
			Credentials shib = new Credentials(tokenMap);
			String shibUin = shib.getUin();
			for(String uin : admins) {
				if(uin.equals(shibUin)) {
					shib.setRole("ROLE_ADMIN");					
				}
			}

			long currentTime = Calendar.getInstance().getTime().getTime()+90000;
			long expTime = Long.parseLong(shib.getExp());
			
			if(expTime < currentTime) {
				System.out.println("Token expired!");	
				((SimpMessagingTemplate) ApplicationContextProvider.appContext.getBean("brokerMessagingTemplate")).convertAndSend(accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResImpl("refresh", "EXPIRED_JWT", new RequestId(requestId)));
				return null;
			}
			else {
				System.out.println("Token expires in: " + ((expTime - currentTime)) / 1000);
			}
			
			Map<String, Object> shibMap = new HashMap<String, Object>();
			
			shibMap.put("shib", shib);
			
			accessor.setSessionAttributes(shibMap);
			
			return MessageBuilder.withPayload("VALID_JWT").setHeaders(accessor).build();
			
		}
		else if("CONNECT".equals(command.name())) {
						
			MessageHeaders headers = message.getHeaders();
		    Map<String, Object> headerMap = (Map<String, Object>) headers.get("nativeHeaders");
		    String jwt = headerMap.get("jwt").toString();
		    
		    if(!"[undefined]".equals(jwt)) {
		    	
		    	jwt = jwt.substring(1, jwt.length()-1);
		    	
		    	Jwt token = null;
		    	
		    	Map<String, String> tokenMap = null;
		    	try {
		    		token = JwtHelper.decodeAndVerify(jwt, hmac);
		    		tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
		    	} catch (Exception e) {
		    		System.out.println("Invalid token!");
		    		return MessageBuilder.withPayload("INVALID_JWT").setHeaders(accessor).build();
		    	}					
		    	Credentials shib = new Credentials(tokenMap);	
		    	String shibUin = shib.getUin();
				for(String uin : admins) {
					if(uin.equals(shibUin)) {
						shib.setRole("ROLE_ADMIN");					
					}
				}
		    	
				System.out.println(shib.getFirstName() + " " + shib.getLastName() + " connected with session id " + headers.get("simpSessionId"));
				
				System.out.println(Long.parseLong(shib.getUin()));
				
		    	if(userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) {
		    		
		    		
		    		UserImpl newUser = new UserImpl();
		    		
					newUser.setUin(Long.parseLong(shib.getUin()));					
					newUser.setRole(shib.getRole());
					
					
					userRepo.save(newUser);
				}
		    	
		    }
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
