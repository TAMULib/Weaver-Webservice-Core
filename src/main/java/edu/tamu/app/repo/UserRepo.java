package edu.tamu.app.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.impl.UserImpl;

@Repository
public interface UserRepo extends CrudRepository<UserImpl, Long>{
	
	public UserImpl getUserByUin(Long uin);
	
}
