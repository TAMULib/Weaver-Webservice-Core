/* 
 * CoreTheme.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/** 
 * Core Theme entity.
 * 
 * @author
 *
 */
@Entity
public class CoreTheme {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column
	private String name;
	
	@Column
	private Boolean active = false;
	
	@OneToMany(mappedBy="theme", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REMOVE, CascadeType.REFRESH}, fetch = FetchType.EAGER, orphanRemoval = true)	
	private Set<ThemeProperty> properties = new HashSet<ThemeProperty>();
	
	public CoreTheme() {}
	
	public CoreTheme(String name) {
		this.name = name;
	}

	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * 
	 * @return      name
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param      name        String
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean isActive) {
		this.active = isActive;
	}
	
	public Set<ThemeProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<ThemeProperty> properties) {
		this.properties = properties;
	}
	
	public void addProperty(ThemeProperty property) {
		properties.add(property);
	}
	
	public void removeProperty(ThemeProperty property) {
		properties.remove(property);
	}
	
	public void clearProperties() {
		properties = new HashSet<ThemeProperty>();
	}

}
