package edu.tamu.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.repo.UserRepo;

@RestController
@RequestMapping("rest/user")
@MessageMapping("/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;
			
	@RequestMapping("/list")
	@MessageMapping("/list")
	@SendTo("/channel/user")
	public ApiResImpl list() {		
		Iterable<UserImpl> users = userRepo.findAll();	
    	return users != null ? new ApiResImpl("success", users) :  new ApiResImpl("fail");
    }
	
	@RequestMapping("/find")
	@MessageMapping("/find")
	@SendTo("/channel/user")
    public ApiResImpl find(@RequestParam() Map<String, String> params) {
    	
		String uinString = params.get("uin");
		
		if("".equals(uinString)) return new ApiResImpl("fail", "No UIN was suplied.");
		
		Long uin = Long.parseLong(uinString);
		UserImpl user = userRepo.getUserByUin(uin);
		
    	return user == null ? new ApiResImpl("fail", "No user wa found with that UIN.") : new ApiResImpl("success", user);
    }
	
    @RequestMapping("/create")
    @MessageMapping("/create")
	@SendTo("/channel/user")
    public ApiResImpl create(@RequestParam() Map<String, String> params) {
    	
    	Long uin = Long.parseLong(params.get("uin"));
    	String firstName = params.get("first-name");
    	String lastName = params.get("last-name");
    	String email = params.get("email");
    	
    	UserImpl user = userRepo.getUserByUin(uin);
    	
    	if(user != null) return new ApiResImpl ("fail", "User with uin: "+uin+" already exist");

    	user = new UserImpl();
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	user.setEmail(email);
    	user.setUIN(uin);
	    
    	userRepo.save(user);
    	
    	return new ApiResImpl("sucsess", user);
   
   }
    
    @RequestMapping("/delete")
    @MessageMapping("/delete")
	@SendTo("/channel/user")
    public ApiResImpl delete(@RequestParam() Map<String, String> params) {
    	
    	Long uin = Long.parseLong(params.get("uin"));  	
    	UserImpl user = userRepo.getUserByUin(uin);
	    
    	if(user != null) userRepo.delete(user);
    	
    	return user != null ? new ApiResImpl("success") : new ApiResImpl("fail");
   
   }
   
    @RequestMapping("/update")
    @MessageMapping("/update")
	@SendTo("/channel/user")
    public ApiResImpl update(@RequestParam() Map<String, String> params) {

    	Long uin = Long.parseLong(params.get("uin"));
    	String firstName = params.get("first-name");
    	String lastName = params.get("last-name");
    	String email = params.get("email");
    	
    	UserImpl user = userRepo.getUserByUin(uin);
    	
    	if(user != null) {
	    	if(!"".equals(firstName)) user.setFirstName(firstName);
	    	if(!"".equals(lastName)) user.setLastName(lastName);
	    	if(!"".equals(email)) user.setEmail(email);
    	} else {
    		return new ApiResImpl("fail");
    	}
		    
    	userRepo.save(user);
    	
    	return new ApiResImpl("sucsess", user);
   
   }
   
    
    
    
}