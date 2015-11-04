package edu.tamu.app.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.framework.interceptor.CoreRestInterceptor;
import edu.tamu.framework.model.Credentials;

public class AppRestInterceptor extends CoreRestInterceptor {

    @Autowired
    private UserRepo userRepo;
    
    @Value("${app.authority.admins}")
    private String[] admins;
    
//    @Autowired @Lazy
//    private SimpMessagingTemplate simpMessagingTemplate;
    
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
            
            //TODO: switch to using create method on userRepo
            
            AppUser newUser = new AppUser();
            
            newUser.setUin(Long.parseLong(shib.getUin()));                  
            newUser.setRole(shib.getRole());
            
            newUser.setFirstName(shib.getFirstName());
            newUser.setLastName(shib.getLastName());
            
            userRepo.save(newUser);
            
//            Map<String, Object> userMap = new HashMap<String, Object>();
//            
//            userMap.put("list", userRepo.findAll());
//            
//            this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
        }
        else {  
            shib.setRole(user.getRole());
        }
        
        return shib;
    }

}