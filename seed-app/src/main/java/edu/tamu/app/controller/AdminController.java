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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.RequestId;

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
	public ApiResponse confirmUser(@Shib Object shibObj, @ReqId String requestId) throws Exception {

		Credentials shib = (Credentials) shibObj;
		
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("changedUserUin", shib.getUin());
		
		if(userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) {
    		
    		AppUser newUser = new AppUser();
    		
			newUser.setUin(Long.parseLong(shib.getUin()));					
			newUser.setRole(shib.getRole());
			
			userRepo.save(newUser);
			
			newUser.setFirstName(shib.getFirstName());
    		newUser.setLastName(shib.getLastName());
			
			userMap.put("list", userRepo.findAll());
			
			this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse("success", userMap, new RequestId(requestId)));
			
			return new ApiResponse("success", userMap, new RequestId(requestId));
		}
		
		userMap.put("list", userRepo.findAll());
				
		return new ApiResponse("success", userMap, new RequestId(requestId));
	}
	
}