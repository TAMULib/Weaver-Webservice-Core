/* 
 * Credentials.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import java.util.Map;

/**
 * Credenitals object.
 * 
 * @author
 *
 */
public class Credentials {
	
	//private String uid;
	private String lastName;
	private String firstName;
	//private String otherNames;
	//private String entitlement;
	private String netid;
	//private String affiliation;
	//private String scopedAffiliation; 
	//private String orcid;
	private String uin;
	private String exp;
	private String email;
	private String role;
	
	/**
	 * Constructor
	 * 
	 * @param 		token			Map<String, String>
	 * 
	 */
	public Credentials(Map<String, String> token) {
		
		//this.uid = token.get("uid");
		this.lastName = token.get("lastName");
		this.firstName = token.get("firstName");
		//this.otherNames = token.get("otherNames");
		//this.entitlement = token.get("entitlement");
		this.netid = token.get("netid");
		//this.affiliation = token.get("affiliation");
		//this.scopedAffiliation = token.get("scopedAffiliation");
		//this.orcid = token.get("orcid");
		this.uin = token.get("uin");
		this.exp = token.get("exp");
		this.email = token.get("email");
		this.role = token.get("role");
	}
	
	/*
	public String getUid() {
		return this.uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	*/
	
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
	
	/*
	public String getOtherNames() {
		return this.otherNames;
	}
	
	public void setOtherNames(String otherNames) {
		this.otherNames = otherNames;
	}
	
	public String getEntitlement() {
		return this.entitlement;
	}
	
	public void setEntitlement(String entitlement) {
		this.entitlement = entitlement;
	}
	*/
	
	/**
	 * Gets netid.
	 * 
	 * @return		String
	 * 
	 */
	public String getNetId() {
		return this.netid;
	}
	
	/**
	 * Sets netid.
	 * 
	 * @param 		netid			String
	 * 
	 */
	public void setNetId(String netid) {
		this.netid = netid;
	}
	
	/*
	public String getAffiliation() {
		return this.affiliation;
	}
	
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	
	public String getScopedAffiliation() {
		return this.scopedAffiliation;
	}
	
	public void setScopedAffiliation(String scopedAffiliation) {
		this.scopedAffiliation = scopedAffiliation;
	}
	
	public String getOrcid() {
		return this.orcid;
	}
	
	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}
	*/
	
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
}
