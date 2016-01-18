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
import javax.persistence.OneToOne;

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
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = CoreTheme.class, property = "id") 
	@JsonIdentityReference(alwaysAsId = false)
	private CoreTheme theme;	

	@OneToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = ThemePropertyName.class, property = "id") 
	@JsonIdentityReference(alwaysAsId = false)
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
