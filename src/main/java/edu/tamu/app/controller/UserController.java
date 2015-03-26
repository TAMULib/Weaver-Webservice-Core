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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Credentials;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
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
	public ApiResImpl credentials(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);		
		Credentials shib = (Credentials) accessor.getSessionAttributes().get("shib");
		if(shib != null && userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) 
			return new ApiResImpl("failure", "user not registered");		
		return shib != null ? credentials(shib, accessor.getNativeHeader("id").get(0)) : new ApiResImpl("refresh", "EXPIRED_JWT", new RequestId(accessor.getNativeHeader("id").get(0)));
	}

	/**
	 * Method to pack credentials into ApiResImp.
	 * 
	 * @param 		shib			Credentials
	 * 
	 * @return		ApiResImpl
	 * 
	 */
	private ApiResImpl credentials(Credentials shib, String id) {
		System.out.println("Creating credentials with id " + id);
		//TODO: all business logic for credentials should take place here 
		//      calling methods will just obtain credentials
		return new ApiResImpl("success", shib, new RequestId(id));
	}

}
