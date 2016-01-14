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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Entity
@Table(name="core_users")
public abstract class AbstractCoreUserImpl implements CoreUser {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="uin", nullable = true)
	private Long uin;
	
	@Column(name="role")
	private String role;
	
	public AbstractCoreUserImpl() {}
	
	public AbstractCoreUserImpl(Long uin) {
		this.uin = uin;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
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
