package edu.tamu.app.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.api.RESTres;
import edu.tamu.app.model.api.WSin;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.repo.UserRepo;

@Controller
@RestController
public class UserController {

	@Autowired
	private UserRepo userRepo;
	
	private final static String REST_MAPPING_PREFIX = "rest/user";
	private final static String WS_MAPPING = "/user";
	private final static String WS_CHANNEL = "channel/user";
	
	@MessageMapping(WS_MAPPING)
	public void WSRouter(WSin wsIn) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if(wsIn.getAction() != null) {
			
	
			switch (wsIn.getAction()) {
		        case "list":    System.out.println(wsIn.getAction());
		        				this.list();
		        				break;
		        default: System.out.println("No action");
		                 break;
		    }
		}
	}
	
	@RequestMapping(REST_MAPPING_PREFIX+"/list")
	@SendTo(WS_CHANNEL)
    public RESTres list() {
		
		System.out.println("Listing users");
		
		Iterable<UserImpl> users = userRepo.findAll();
		
    	return users != null ? new RESTres("success", users) :  new RESTres("fail");
   
    }
	
	@RequestMapping(REST_MAPPING_PREFIX+"/find")
	@SendTo(WS_CHANNEL)
    public RESTres find(@RequestParam(value="uin") String uinString) {
    	Long uin = Long.parseLong(uinString);
    	return new RESTres("success", userRepo.getUserByUin(uin));
    }
	
    @RequestMapping(REST_MAPPING_PREFIX+"/create")
    @SendTo(WS_CHANNEL)
    public RESTres create(
    		@RequestParam(value="first-name", defaultValue="") String firstName, 
    		@RequestParam(value="last-name", defaultValue="") String lastName, 
    		@RequestParam(value="email", defaultValue="") String email,
    		@RequestParam(value="uin", defaultValue="") String uinString
    	) {
    	
    	Long uin = Long.parseLong(uinString);  	
    	UserImpl user = userRepo.getUserByUin(uin);
    	
    	if(user != null) return new RESTres ("fail", "User with uin: "+uin+" already exist");

    	user = new UserImpl();
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	user.setEmail(email);
    	user.setUIN(uin);
	    
    	userRepo.save(user);
    	
    	return new RESTres("sucsess", user);
   
   }
    
    @RequestMapping(REST_MAPPING_PREFIX+"/delete")
    @SendTo(WS_CHANNEL)
    public RESTres delete(@RequestParam(value="uin", defaultValue="") String uinString) {
    	
    	Long uin = Long.parseLong(uinString);  	
    	UserImpl user = userRepo.getUserByUin(uin);
	    
    	if(user != null) userRepo.delete(user);
    	
    	return user != null ? new RESTres("success") : new RESTres("fail");
   
   }
   
    @RequestMapping(REST_MAPPING_PREFIX+"/update")
    @SendTo(WS_CHANNEL)
    public RESTres update(
    		@RequestParam(value="uin", defaultValue="") String uinString,
    		@RequestParam(value="first-name", defaultValue="") String firstName, 
    		@RequestParam(value="last-name", defaultValue="") String lastName, 
    		@RequestParam(value="email", defaultValue="") String email
    	) {

    	Long uin = Long.parseLong(uinString);
    	UserImpl user = userRepo.getUserByUin(uin);
    	
    	if(user != null) {
	    	if(!"".equals(firstName)) user.setFirstName(firstName);
	    	if(!"".equals(lastName)) user.setLastName(lastName);
	    	if(!"".equals(email)) user.setEmail(email);
    	} else {
    		return new RESTres("fail");
    	}
		    
    	userRepo.save(user);
    	
    	return new RESTres("sucsess", user);
   
   }
   
    
    
    
}