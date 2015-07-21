/* 
 * UserRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.AppUser;

/**
 * User repository.
 * 
 * @author
 *
 */
@Repository
public interface UserRepo extends JpaRepository <AppUser, Long>, UserRepoCustom {
	
	/**
	 * Retrieve user by UIN.
	 * 
	 * @param 		uin				Long
	 * 
	 * @return		UserImpl
	 * 
	 */
	public AppUser getUserByUin(Long uin);

}
