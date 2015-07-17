/* 
 * UserImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import javax.persistence.Entity;

import edu.tamu.framework.model.AbstractCoreUserImpl;

@Entity
public class AppUser extends AbstractCoreUserImpl{
	
	public AppUser() {
		super();
	}
	
	public AppUser(Long uin) {
		super(uin);
	}
	
}
