/* 
 * ThemePropertyRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.model.repo.custom;

import edu.tamu.weaver.model.ThemeProperty;
import edu.tamu.weaver.model.ThemePropertyName;

public interface ThemePropertyRepoCustom {

    public ThemeProperty create(ThemePropertyName propertyName, String value);

}
