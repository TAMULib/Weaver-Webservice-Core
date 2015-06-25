/* 
 * UserController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
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
 * User Controller
 * 
 * @author
 *
 */
@RestController
@RequestMapping("rest/user")
@MessageMapping("/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 

	/**
	 * Websocket endpoint to request credentials.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/credentials")
	@SendToUser
	@Auth(role="ROLE_USER")
	public ApiResImpl credentials(Message<?> message, @Shib Object credentials, @ReqId String requestId) throws Exception {
		
		Credentials shib = (Credentials) credentials;
		shib.setRole(userRepo.getUserByUin(Long.parseLong(shib.getUin())).getRole());
		
		if(shib != null && userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) 
			return new ApiResImpl("failure", "user not registered");		
		return shib != null ? new ApiResImpl("success", shib, new RequestId(requestId)) : new ApiResImpl("refresh", "EXPIRED_JWT", new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return all users.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/all")
	@SendToUser
	@Auth(role="ROLE_MANAGER")
	public ApiResImpl allUsers(Message<?> message, @ReqId String requestId) throws Exception {
			
		Map<String,List<UserImpl>> map = new HashMap<String,List<UserImpl>>();
		map.put("list", userRepo.findAll());	
		
		return new ApiResImpl("success", map, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to update users role.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/update-role")
	@SendToUser
	@Auth(role="ROLE_MANAGER")
	public ApiResImpl updateRole(Message<?> message, @ReqId String requestId) throws Exception {		
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String data = accessor.getNativeHeader("data").get(0).toString();		

		Map<String,String> map = new HashMap<String,String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		UserImpl user = userRepo.getUserByUin(Long.decode(map.get("uin")));		
		user.setRole(map.get("role"));		
		userRepo.save(user);
		
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("list", userRepo.findAll());
		userMap.put("changedUserUin", map.get("uin"));
		
		this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResImpl("success", userMap, new RequestId(requestId)));
		
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}

}
