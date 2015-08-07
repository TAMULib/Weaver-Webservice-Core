/* 
 * Credentials.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import java.util.Map;

/**
 * Credenitals object.
 * 
 * @author
 *
 */
public class Credentials {
	
	private String lastName;
	private String firstName;
	private String netid;
	private String uin;
	private String exp;
	private String email;
	private String role;
	private String affiliation;
		
	public Credentials() {}
	
	/**
	 * Constructor
	 * 
	 * @param 		token			Map<String, String>
	 * 
	 */
	public Credentials(Map<String, String> token) {		
		this.lastName = token.get("lastName");
		this.firstName = token.get("firstName");
		this.netid = token.get("netid");
		this.uin = token.get("uin");
		this.exp = token.get("exp");
		this.email = token.get("email");
		this.role = token.get("role");
		this.affiliation = token.get("affiliation");
	}
	
	/**
	 * Gets last name.
	 * 
	 * @return		String
	 * 
	 */
	public String getLastName() {
		return this.lastName;
	}
	
	/**
	 * Sets last name.
	 * 
	 * @param 		lastName		String
	 * 
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Gets first name.
	 * 
	 * @return		String
	 * 
	 */
	public String getFirstName() {
		return this.firstName;
	}
	
	/**
	 * Sets firstname.
	 * 
	 * @param 		firstName		String
	 * 
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
		
	/**
	 * Gets UIN.
	 * 
	 * @return		String
	 * 
	 */
	public String getUin() {
		return this.uin;
	}
	
	/**
	 * Sets UIN.
	 * 
	 * @param 		uin				String
	 * 
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}
	
	/**
	 * Gets expiration.
	 * 
	 * @return		String
	 * 
	 */
	public String getExp() {
		return this.exp;
	}
	
	/**
	 * Sets expiration.
	 * 
	 * @param 		exp				String
	 * 
	 */
	public void setExp(String exp) {
		this.exp = exp;
	}
	
	/**
	 * Gets email.
	 * 
	 * @return		String
	 * 
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Sets email.
	 * 
	 * @param 		email			String
	 * 
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Gets role.
	 * 
	 * @return		String
	 * 
	 */
	public String getRole() {
		return this.role;
	}
	
	/**
	 * Sets role.
	 * 
	 * @param 		role			String
	 * 
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the netid
	 */
	public String getNetid() {
		return netid;
	}

	/**
	 * @param netid the netid to set
	 */
	public void setNetid(String netid) {
		this.netid = netid;
	}
	
	/**
	 * @return affiliation
	 */
	public String getAffiliation() {
		return affiliation;
	}

	/**
	 * @param affiliation
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	
}
