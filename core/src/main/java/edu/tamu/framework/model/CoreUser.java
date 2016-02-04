/* 
 * CoreUser.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

/**
 * Core user interface. lol
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public interface CoreUser {
	
	/**
	 * Sets UIN.
	 * 
	 * @param uin
	 *            Long
	 * 
	 */
	public void setUin(Long uin);

	/**
	 * Gets UIN.
	 * 
	 * @return Long
	 * 
	 */
	public Long getUin();

	/**
	 * Sets role.
	 * 
	 * @param role
	 *            String
	 * 
	 */
	public void setRole(String role);

	/**
	 * Gets role.
	 * 
	 * @return String
	 * 
	 */
	public String getRole();

}
