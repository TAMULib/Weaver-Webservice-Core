/* 
 * AbstractCoreUserImpl.java 
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

import edu.tamu.framework.model.CoreUser;

@Entity
@Table(name="core_users")
public abstract class AbstractCoreUserImpl implements CoreUser {
	
	@Id
	@Column(name="uin", nullable=false)
	private Long uin;
	
	@Column(name="role")
	private String role;
	
	public AbstractCoreUserImpl() {}
	
	public AbstractCoreUserImpl(Long uin) {
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
