package edu.tamu.app.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.tamu.app.model.User;

@Entity
@Table(name="all_users")
public class UserImpl implements User{
	
	@Id
	@Column(name="uin", nullable=false)
	private Long uin;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email")
	private String email;
	
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Override
	public String getFirstName() {
		return firstName;
	}
	
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String getLastName() {
		return lastName;
	}
	
	@Override
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String getEmail() {
		return email;
	}
	
	@Override
	public void setUIN(Long uin) {
		this.uin = uin;
	}
	
	@Override
	public Long getUIN() {
		return uin;
	}

}
