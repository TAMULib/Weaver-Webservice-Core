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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.REFRESH;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

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
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/credentials")
	@SendToUser
	@Auth
	public ApiResponse credentials(@Shib Object credentials) throws Exception {
		
		Credentials shib = (Credentials) credentials;
		shib.setRole(userRepo.getUserByUin(Long.parseLong(shib.getUin())).getRole());
		
		if(shib != null && userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) 
			return new ApiResponse(ERROR, "user not registered");		
		return shib != null ? new ApiResponse(SUCCESS, shib) : new ApiResponse(REFRESH, "EXPIRED_JWT");
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
	public ApiResponse allUsers() throws Exception {
			
		Map<String,List<AppUser>> map = new HashMap<String,List<AppUser>>();
		map.put("list", userRepo.findAll());	
		
		return new ApiResponse(SUCCESS, map);
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
	public ApiResponse updateRole(@Data String data) throws Exception {		
		
		Map<String,String> map = new HashMap<String,String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		AppUser user = userRepo.getUserByUin(Long.decode(map.get("uin")));		
		user.setRole(map.get("role"));		
		userRepo.save(user);
		
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("list", userRepo.findAll());
		userMap.put("changedUserUin", map.get("uin"));
		
		this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
		
		return new ApiResponse(SUCCESS, "ok");
	}

}
