package edu.tamu.app.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.impl.UserImpl;

@Repository
public interface UserRepo extends JpaRepository <UserImpl, Long>{
	
	public UserImpl getUserByUin(Long uin);
	
}
