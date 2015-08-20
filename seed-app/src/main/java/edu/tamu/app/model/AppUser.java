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

import javax.persistence.Column;
import javax.persistence.Entity;

import edu.tamu.framework.model.AbstractCoreUserImpl;

@Entity
public class AppUser extends AbstractCoreUserImpl {
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	public AppUser() {
		super();
	}
	
	public AppUser(Long uin) {
		super(uin);
	}
	
	/**
	 * 
	 * @return      firstName
	 * 
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param      firstName        String
	 * 
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return      lastName
	 * 
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param      lastName        String
	 * 
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
