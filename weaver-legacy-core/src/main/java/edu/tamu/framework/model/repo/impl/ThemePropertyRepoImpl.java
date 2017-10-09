/* 
 * ThemePropertyRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;
import edu.tamu.framework.model.repo.ThemePropertyRepo;
import edu.tamu.framework.model.repo.custom.ThemePropertyRepoCustom;

public class ThemePropertyRepoImpl implements ThemePropertyRepoCustom {

    @Autowired
    private ThemePropertyRepo themePropertyRepo;

    @Override
    public ThemeProperty create(ThemePropertyName propertyName, String value) {
        return themePropertyRepo.save(new ThemeProperty(propertyName, value));
    }

}
