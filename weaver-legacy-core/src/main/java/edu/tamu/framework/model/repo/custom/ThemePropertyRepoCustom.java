/* 
 * ThemePropertyRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.repo.custom;

import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;

public interface ThemePropertyRepoCustom {

    public ThemeProperty create(ThemePropertyName propertyName, String value);

}
