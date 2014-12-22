package edu.tamu.app.model;

public interface User {

	/**
	 * @parameter User's first name.
	 */
	public void setFirstName(String firstName);

	/**
	 * @return User's first name as a String.
	 */
	public String getFirstName();
	
	/**
	 * @parameter User's last name.
	 */
	public void setLastName(String lastName);
	
	/**
	 * @return User's last name as a String.
	 */
	public String getLastName();
	
	/**
	 * @parameter User's email.
	 */
	public void setEmail(String email);
	
	/**
	 * @return User's email as a String.
	 */
	public String getEmail();

	/**
	 * @parameter User's UIN.
	 */
	public void setUIN(Long uin);
	
	/**
	 * @return User's UIN as a Long.
	 */
	public Long getUIN();
}

