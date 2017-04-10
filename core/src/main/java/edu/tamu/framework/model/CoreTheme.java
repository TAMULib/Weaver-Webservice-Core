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
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Core Theme entity.
 * 
 * @author
 *
 */
@Entity
public class CoreTheme extends BaseEntity {

    @Column
    private String name;

    @Column
    private Boolean active = false;

    @OneToMany(mappedBy = "theme", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
    @Fetch(FetchMode.SELECT)
    private Set<ThemeProperty> themeProperties = new HashSet<ThemeProperty>();

    public CoreTheme() {
    }

    public CoreTheme(String name) {
        this();
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the themeProperties
     */
    public Set<ThemeProperty> getThemeProperties() {
        return themeProperties;
    }

    /**
     * @param themeProperties
     *            the themeProperties to set
     */
    public void setThemeProperties(Set<ThemeProperty> themeProperties) {
        this.themeProperties = themeProperties;
    }

    public void addThemeProperty(ThemeProperty themeProperty) {
        themeProperties.add(themeProperty);
    }

    public void removeThemeProperty(ThemeProperty themeProperty) {
        themeProperties.remove(themeProperty);
    }

    public void clearThemeProperties() {
        themeProperties.clear();
    }

}
