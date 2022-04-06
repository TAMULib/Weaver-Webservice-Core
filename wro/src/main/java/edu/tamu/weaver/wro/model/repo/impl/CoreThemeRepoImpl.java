package edu.tamu.weaver.wro.model.repo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.wro.model.CoreTheme;
import edu.tamu.weaver.wro.model.ThemeProperty;
import edu.tamu.weaver.wro.model.ThemePropertyName;
import edu.tamu.weaver.wro.model.repo.CoreThemeRepo;
import edu.tamu.weaver.wro.model.repo.ThemePropertyNameRepo;
import edu.tamu.weaver.wro.model.repo.ThemePropertyRepo;
import edu.tamu.weaver.wro.model.repo.custom.CoreThemeRepoCustom;

public class CoreThemeRepoImpl extends AbstractWeaverRepoImpl<CoreTheme, CoreThemeRepo> implements CoreThemeRepoCustom {

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
        CoreTheme theme = coreThemeRepo.save(new CoreTheme(name));
        for (ThemePropertyName themePropertyName : themePropertyNameRepo.findAll()) {
            theme = addThemeProperty(theme, themePropertyRepo.create(themePropertyName, null));
        }
        return super.create(theme);
    }

    public CoreTheme addThemeProperty(CoreTheme theme, ThemeProperty themeProperty) {
        theme.addThemeProperty(themeProperty);
        themeProperty.setTheme(theme);
        themePropertyRepo.save(themeProperty);
        return theme;
    }

    @Override
    public void updateActiveTheme(CoreTheme theme) {
        CoreTheme activeTheme = coreThemeRepo.findByActiveTrue();
        if (activeTheme != null) {
            activeTheme.setActive(false);
            super.update(activeTheme);
        }
        theme.setActive(true);
        super.update(theme);
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
        themePropertyRepo.update(themeProperty);
        super.update(theme);
    }

    @Override
    @Transactional
    public void delete(CoreTheme theme) {

        List<ThemePropertyName> themPropertyNamesToDelete = new ArrayList<ThemePropertyName>();
        List<ThemeProperty> themPropertiesToDelete = new ArrayList<ThemeProperty>();

        theme.getThemeProperties().forEach(themeProperty -> {
            Long themePropertyId = themeProperty.getThemePropertyName().getId();
            themeProperty.setThemePropertyName(null);

            themePropertyRepo.update(themeProperty);

            ThemePropertyName themePropertyName = themePropertyNameRepo.getById(themePropertyId);
            if (themePropertyRepo.findThemePropertyByThemePropertyName(themePropertyName).isEmpty()) {
                themPropertyNamesToDelete.add(themePropertyName);
            }

            themeProperty.setTheme(null);
            themePropertyRepo.update(themeProperty);

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

        super.delete(theme);
    }

    @Override
    protected String getChannel() {
        return "/channel/theme";
    }

}
