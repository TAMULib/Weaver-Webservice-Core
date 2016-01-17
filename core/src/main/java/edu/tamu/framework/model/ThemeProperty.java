/* 
 * ThemeProperty.java 
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/** 
 * Application ThemeProperty entity.
 * 
 * @author
 *
 */
@Entity
public class ThemeProperty {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column
	private String value;
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = CoreTheme.class, property = "name") 
	@JsonIdentityReference(alwaysAsId = true)
	private CoreTheme theme;	

	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = ThemePropertyName.class, property = "name") 
	@JsonIdentityReference(alwaysAsId = true)
	private ThemePropertyName themePropertyName;
	
	public ThemeProperty() {}
	
	public ThemeProperty(ThemePropertyName propertyName, String value) {
		this.value = value;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setThemePropertyName(ThemePropertyName propertyName) {
		this.themePropertyName = propertyName;
	}
	
	public ThemePropertyName getPropertyName() {
		return this.themePropertyName;
	}

}
