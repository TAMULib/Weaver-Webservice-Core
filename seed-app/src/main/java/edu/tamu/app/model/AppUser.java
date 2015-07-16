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

import edu.tamu.framework.model.AbstractUserImpl;

@Entity
public class AppUser extends AbstractUserImpl{
	
	public AppUser() {
		super();
	}
	
	public AppUser(Long uin) {
		super(uin);
	}
	
}
