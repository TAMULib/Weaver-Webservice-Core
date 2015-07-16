package edu.tamu.app.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.model.AppUser;

import edu.tamu.framework.controller.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;
import edu.tamu.app.model.repo.UserRepo;

public class AppStompInterceptor extends CoreStompInterceptor {
	
	@Autowired
	private UserRepo userRepo;
	
	@Value("${app.authority.admins}")
	private String[] admins;

	@Override
	public Credentials confirmCreateUser(Credentials shib) {
		
		AppUser user = userRepo.getUserByUin(Long.parseLong(shib.getUin()));
		
		if(user == null) {
    		
    		if(shib.getRole() == null) {
    			shib.setRole("ROLE_USER");
    		}
        	String shibUin = shib.getUin();
    		for(String uin : admins) {
    			if(uin.equals(shibUin)) {
    				shib.setRole("ROLE_ADMIN");					
    			}
    		}
    		
    		AppUser newUser = new AppUser();
    		
    		newUser.setUin(Long.parseLong(shib.getUin()));					
    		newUser.setRole(shib.getRole());
    		
    		userRepo.save(newUser);
        	
        	//System.out.println(shib.getFirstName() + " " + shib.getLastName() + " connected with session id " + headers.get("simpSessionId"));
    		
    		System.out.println(Long.parseLong(shib.getUin()));	
    
    	}
    	else {
    		shib.setRole(user.getRole());
    	}
		
		return shib;
		
	}

	
	
}
