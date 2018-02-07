/* 
 * ThemePropertyRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.weaver.model.ThemeProperty;
import edu.tamu.weaver.model.ThemePropertyName;
import edu.tamu.weaver.model.repo.ThemePropertyRepo;
import edu.tamu.weaver.model.repo.custom.ThemePropertyRepoCustom;

public class ThemePropertyRepoImpl implements ThemePropertyRepoCustom {

    @Autowired
    private ThemePropertyRepo themePropertyRepo;

    @Override
    public ThemeProperty create(ThemePropertyName propertyName, String value) {
        return themePropertyRepo.save(new ThemeProperty(propertyName, value));
    }

}
