package edu.tamu.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.api.apiResImpl;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.repo.UserRepo;

@RestController
@RequestMapping("rest/user")
@MessageMapping("/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;
			
	@RequestMapping("/list")
	@MessageMapping("/list")
	@SendTo("/channel/user")
	public apiResImpl list() {		
		Iterable<UserImpl> users = userRepo.findAll();	
    	return users != null ? new apiResImpl("success", users) :  new apiResImpl("fail");
    }
	
	@RequestMapping("/find")
	@MessageMapping("/find")
	@SendTo("/channel/user")
    public apiResImpl find(@RequestParam() Map<String, String> params) {
    	
		String uinString = params.get("uin");
		
		if("".equals(uinString)) return new apiResImpl("fail", "No UIN was suplied.");
		
		Long uin = Long.parseLong(uinString);
		UserImpl user = userRepo.getUserByUin(uin);
		
    	return user == null ? new apiResImpl("fail", "No user wa found with that UIN.") : new apiResImpl("success", user);
    }
	
    @RequestMapping("/create")
    @MessageMapping("/create")
	@SendTo("/channel/user")
    public apiResImpl create(@RequestParam() Map<String, String> params) {
    	
    	Long uin = Long.parseLong(params.get("uin"));
    	String firstName = params.get("first-name");
    	String lastName = params.get("last-name");
    	String email = params.get("email");
    	
    	UserImpl user = userRepo.getUserByUin(uin);
    	
    	if(user != null) return new apiResImpl ("fail", "User with uin: "+uin+" already exist");

    	user = new UserImpl();
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	user.setEmail(email);
    	user.setUIN(uin);
	    
    	userRepo.save(user);
    	
    	return new apiResImpl("sucsess", user);
   
   }
    
    @RequestMapping("/delete")
    @MessageMapping("/delete")
	@SendTo("/channel/user")
    public apiResImpl delete(@RequestParam() Map<String, String> params) {
    	
    	Long uin = Long.parseLong(params.get("uin"));  	
    	UserImpl user = userRepo.getUserByUin(uin);
	    
    	if(user != null) userRepo.delete(user);
    	
    	return user != null ? new apiResImpl("success") : new apiResImpl("fail");
   
   }
   
    @RequestMapping("/update")
    @MessageMapping("/update")
	@SendTo("/channel/user")
    public apiResImpl update(@RequestParam() Map<String, String> params) {

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
    		return new apiResImpl("fail");
    	}
		    
    	userRepo.save(user);
    	
    	return new apiResImpl("sucsess", user);
   
   }
   
    
    
    
}