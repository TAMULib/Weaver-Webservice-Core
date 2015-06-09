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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.aspect.annotation.Auth;
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
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
	@MessageMapping("/confirm-user")
	@SendToUser
	@Auth(role="ROLE_ADMIN")
	public ApiResImpl confirmUser(Message<?> message, @Shib Object shibObj, @ReqId String requestId) throws Exception {

		Credentials shib = (Credentials) shibObj;
		
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("changedUserUin", shib.getUin());
		
		if(userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) {
    		
    		UserImpl newUser = new UserImpl();
    		
			newUser.setUin(Long.parseLong(shib.getUin()));					
			newUser.setRole(shib.getRole());
			
			userRepo.save(newUser);
			
			userMap.put("list", userRepo.findAll());
			
			this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResImpl("success", userMap, new RequestId(requestId)));
			
			return new ApiResImpl("success", userMap, new RequestId(requestId));
		}
		
		userMap.put("list", userRepo.findAll());
				
		return new ApiResImpl("success", userMap, new RequestId(requestId));
	}
	
}