package edu.tamu.app.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.framework.aspect.ControllerAspect;


@Component
@Aspect
public class AppControllerAspect extends ControllerAspect {

	@Autowired
	UserRepo userRepo;
	
	@Override
	public String getUserRole(String uin) {
		return userRepo.findOne(Long.parseLong(uin)).getRole();
	}

}
