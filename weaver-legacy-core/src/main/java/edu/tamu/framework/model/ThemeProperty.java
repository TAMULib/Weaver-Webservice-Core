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
public class ThemeProperty extends BaseEntity {

    @Column
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = CoreTheme.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private CoreTheme theme;

    @OneToOne(fetch = FetchType.EAGER)
    private ThemePropertyName themePropertyName;

    public ThemeProperty() {
    }

    public ThemeProperty(ThemePropertyName themePropertyName, String value) {
        this();
        this.themePropertyName = themePropertyName;
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the theme
     */
    public CoreTheme getTheme() {
        return theme;
    }

    /**
     * @param theme
     *            the theme to set
     */
    public void setTheme(CoreTheme theme) {
        this.theme = theme;
    }

    /**
     * @return the themePropertyName
     */
    public ThemePropertyName getThemePropertyName() {
        return themePropertyName;
    }

    /**
     * @param themePropertyName
     *            the themePropertyName to set
     */
    public void setThemePropertyName(ThemePropertyName themePropertyName) {
        this.themePropertyName = themePropertyName;
    }

}
