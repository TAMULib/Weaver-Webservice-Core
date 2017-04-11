/* 
 * CoreThemeRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.repo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.tamu.framework.model.CoreTheme;
import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;
import edu.tamu.framework.model.repo.CoreThemeRepo;
import edu.tamu.framework.model.repo.ThemePropertyNameRepo;
import edu.tamu.framework.model.repo.ThemePropertyRepo;
import edu.tamu.framework.model.repo.custom.CoreThemeRepoCustom;

public class CoreThemeRepoImpl implements CoreThemeRepoCustom {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CoreThemeRepo coreThemeRepo;

    @Autowired
    private ThemePropertyRepo themePropertyRepo;

    @Autowired
    private ThemePropertyNameRepo themePropertyNameRepo;

    @Override
    public CoreTheme create(String name) {
        CoreTheme theme = coreThemeRepo.getByName(name);
        if (theme == null) {
            CoreTheme newTheme = coreThemeRepo.save(new CoreTheme(name));
            for (ThemePropertyName themePropertyName : themePropertyNameRepo.findAll()) {
                newTheme = addThemeProperty(newTheme, themePropertyRepo.create(themePropertyName, null));
            }
            return newTheme;
        }
        return theme;
    }

    public CoreTheme addThemeProperty(CoreTheme theme, ThemeProperty themeProperty) {
        theme.addThemeProperty(themeProperty);
        themeProperty.setTheme(theme);
        themePropertyRepo.save(themeProperty);
        return coreThemeRepo.save(theme);
    }

    @Override
    public void updateActiveTheme(CoreTheme theme) {
        CoreTheme activeTheme = coreThemeRepo.findByActiveTrue();
        if (activeTheme != null) {
            activeTheme.setActive(false);
            coreThemeRepo.save(activeTheme);
        }
        theme.setActive(true);
        coreThemeRepo.save(theme);
    }

    @Override
    public void updateThemeProperty(Long themeId, Long themePropertyId, String value) {
        CoreTheme theme = coreThemeRepo.getById(themeId);
        ThemeProperty themeProperty = themePropertyRepo.findThemePropertyById(themePropertyId);
        themeProperty.setValue(value);
        Optional<ThemeProperty> existingThemeProperty = theme.getThemeProperties().stream().filter(tp -> tp.getId() == themePropertyId).findFirst();
        if (existingThemeProperty.isPresent()) {
            existingThemeProperty.get().setValue(value);
        }
        themePropertyRepo.save(themeProperty);
        coreThemeRepo.save(theme);
    }

    @Override
    @Transactional
    public void delete(CoreTheme theme) {

        if (theme != null) {

            List<ThemePropertyName> themPropertyNamesToDelete = new ArrayList<ThemePropertyName>();
            List<ThemeProperty> themPropertiesToDelete = new ArrayList<ThemeProperty>();

            theme.getThemeProperties().forEach(themeProperty -> {
                Long themePropertyId = themeProperty.getThemePropertyName().getId();
                themeProperty.setThemePropertyName(null);

                themePropertyRepo.save(themeProperty);

                ThemePropertyName themePropertyName = themePropertyNameRepo.findOne(themePropertyId);
                if (themePropertyRepo.findThemePropertyByThemePropertyName(themePropertyName).isEmpty()) {
                    themPropertyNamesToDelete.add(themePropertyName);
                }

                themeProperty.setTheme(null);
                themePropertyRepo.save(themeProperty);

                if (coreThemeRepo.findByThemePropertiesId(themeProperty.getId()).isEmpty()) {
                    themPropertiesToDelete.add(themeProperty);
                }

            });

            theme.clearThemeProperties();
            coreThemeRepo.save(theme);

            themPropertyNamesToDelete.forEach(themPropertyName -> {
                themePropertyNameRepo.delete(themPropertyName);
            });

            themPropertiesToDelete.forEach(themeProperty -> {
                themePropertyRepo.delete(themeProperty);
            });

            em.remove(em.contains(theme) ? theme : em.merge(theme));
        }
    }

}
