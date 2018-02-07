/* 
 * ThemePropertyNameRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.weaver.model.ThemePropertyName;
import edu.tamu.weaver.model.repo.ThemePropertyNameRepo;

public class ThemePropertyNameRepoImpl {

    @Autowired
    private ThemePropertyNameRepo themePropertyNameRepo;

    public ThemePropertyName create(String name) {
        ThemePropertyName propertyName = themePropertyNameRepo.getThemePropertyNameByName(name);
        if (propertyName == null) {
            return themePropertyNameRepo.save(new ThemePropertyName(name));
        }
        return propertyName;
    }

}
