package edu.tamu.app.model.repo;

import edu.tamu.app.model.impl.UserImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for user repository.
 * 
 * @author 
 */
@Repository
public interface UserRepo extends JpaRepository <UserImpl, Long>{
	
	/**
	 * 
	 * @param uin
	 * @return UserImpl
	 */
	public UserImpl getUserByUin(Long uin);
	
}
