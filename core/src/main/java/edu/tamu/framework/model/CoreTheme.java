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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
	
	@OneToMany(mappedBy="theme", fetch=FetchType.EAGER, cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
	@Fetch(FetchMode.SELECT)
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, scope=ThemeProperty.class, property="id")
	@JsonIdentityReference(alwaysAsId=false)
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
		return this.active;
	}

	public void setActive(Boolean isActive) {
		this.active = isActive;
	}
	
	public Set<ThemeProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(Set<ThemeProperty> properties) {
		this.properties = properties;
	}
	
	public void addProperty(ThemeProperty property) {
		this.properties.add(property);
	}
	
	public void removeProperty(ThemeProperty property) {
		this.properties.remove(property);
	}
	
	public void clearProperties() {
		this.properties = new HashSet<ThemeProperty>();
	}

}
