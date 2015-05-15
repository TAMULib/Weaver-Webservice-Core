/* 
 * AdminController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.aspect.annotation.ReqId;
import edu.tamu.app.aspect.annotation.Shib;
import edu.tamu.app.model.Credentials;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.model.repo.UserRepo;

/** 
 * Admin Controller.
 * 
 * @author
 *
 */
@RestController
@MessageMapping("/admin")
public class AdminController {
	
	@Autowired
	public ObjectMapper objectMapper;
	
	@Autowired
	private UserRepo userRepo;

	/**
	 * Websocket endpoint to request to broadcast message.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		Map<String, String>
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/broadcast")
	@SendTo("/channel/admin/broadcast")
	public ApiResImpl broadcast(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		
		Map<String, String> messageMap = new HashMap<String, String>();
		messageMap.put("message", accessor.getNativeHeader("data").get(0));
		
		return new ApiResImpl("success", messageMap, new RequestId(accessor.getNativeHeader("id").get(0)));
	}
	
	@MessageMapping("/confirmuser")
	@SendToUser
	public ApiResImpl confirmUser(Message<?> message, @Shib Object shibObj, @ReqId String requestId) throws Exception {

		Credentials shib = (Credentials) shibObj;
		
		if(userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) {
    		
    		UserImpl newUser = new UserImpl();
    		
			newUser.setUin(Long.parseLong(shib.getUin()));					
			newUser.setRole(shib.getRole());
			
			userRepo.save(newUser);
			return new ApiResImpl("success", "created user", new RequestId(requestId));
		}
		
		return new ApiResImpl("success", "user exists", new RequestId(requestId));
	}
	
}