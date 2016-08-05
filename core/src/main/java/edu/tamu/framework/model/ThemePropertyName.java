/* 
 * ThemePropertyName.java 
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


/**
 * Application ThemePropertyName entity.
 * 
 * @author
 *
 */
@Entity
public class ThemePropertyName extends BaseEntity {

    @Column(unique = true)
    private String name;

    public ThemePropertyName() {}

    public ThemePropertyName(String name) {
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

}
