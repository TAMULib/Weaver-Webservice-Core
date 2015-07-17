/* 
 * User.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

/**
 * User interface. lol
 * 
 * @author
 *
 */
public interface CoreUser {

	/**
	 * Sets UIN.
	 * 
	 * @param 		uin				Long
	 * 
	 */
	public void setUin(Long uin);
	
	/**
	 * Gets UIN.
	 * 
	 * @return		Long
	 * 
	 */
	public Long getUin();
	
	/**
	 * Sets role.
	 * 
	 * @param 		role			String
	 * 
	 */
	public void setRole(String role);
	
	/**
	 * Gets role.
	 * 
	 * @return		String
	 * 
	 */
	public String getRole();


}

