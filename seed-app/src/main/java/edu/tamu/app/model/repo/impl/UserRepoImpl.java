package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.app.model.repo.UserRepoCustom;

public class UserRepoImpl implements UserRepoCustom {

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserImpl create(Long uin) {
		UserImpl user = null;
		if(userRepo.getUserByUin(uin)==null) {
			user = new UserImpl(uin);
			userRepo.save(user);
		}
		return user;
	}

}