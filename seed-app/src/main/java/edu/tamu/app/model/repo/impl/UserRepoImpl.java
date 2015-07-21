package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.app.model.repo.UserRepoCustom;

public class UserRepoImpl implements UserRepoCustom {

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public AppUser create(Long uin) {
		AppUser user = null;
		if(userRepo.getUserByUin(uin)==null) {
			user = new AppUser(uin);
			userRepo.save(user);
		}
		return user;
	}

}