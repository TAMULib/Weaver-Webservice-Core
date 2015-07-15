/* 
 * UserImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.tamu.framework.model.User;

@Entity
@Table(name="core_users")
public abstract class AbstractUserImpl implements User{
	
	@Id
	@Column(name="uin", nullable=false)
	private Long uin;
	
	@Column(name="role")
	private String role;
	
	public AbstractUserImpl() {}
	
	public AbstractUserImpl(Long uin) {
		this.uin = uin;
	}
	
	@Override
	public void setUin(Long uin) {
		this.uin = uin;
	}
	
	@Override
	public Long getUin() {
		return uin;
	}

	@Override
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String getRole() {
		return role;
	}
}
