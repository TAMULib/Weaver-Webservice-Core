package edu.tamu.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.api.RESTres;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.repo.UserRepo;

@RestController
public class UserController {

	@Autowired
	private UserRepo userRepo;
	
	private final static String MAPPING_PREFIX = "rest/user";
	
	@RequestMapping(MAPPING_PREFIX+"/list")
    public Iterable<UserImpl> users() {
    	
    	return userRepo.findAll();
   
    }
	
	@RequestMapping(MAPPING_PREFIX+"/find")
    public RESTres user(@RequestParam(value="uin", defaultValue="123456789") String uinString) {
    	Long uin = Long.parseLong(uinString);
    	return new RESTres("success", userRepo.getUserByUin(uin));
    }
	
    @RequestMapping(MAPPING_PREFIX+"/create")
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
    
    @RequestMapping(MAPPING_PREFIX+"/delete")
    public RESTres delete(@RequestParam(value="uin", defaultValue="") String uinString) {
    	
    	Long uin = Long.parseLong(uinString);  	
    	UserImpl user = userRepo.getUserByUin(uin);
	    
    	if(user != null) userRepo.delete(user);
    	
    	return user != null ? new RESTres("success") : new RESTres("fail");
   
   }
   
    @RequestMapping(MAPPING_PREFIX+"/update")
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