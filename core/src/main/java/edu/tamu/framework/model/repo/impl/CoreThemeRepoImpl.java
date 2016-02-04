package edu.tamu.framework.model.repo.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.framework.model.CoreTheme;
import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.repo.CoreThemeRepo;
import edu.tamu.framework.model.repo.CoreThemeRepoCustom;
import edu.tamu.framework.model.repo.ThemePropertyNameRepo;
import edu.tamu.framework.model.repo.ThemePropertyRepo;

public class CoreThemeRepoImpl implements CoreThemeRepoCustom {
	
	@Autowired
	private CoreThemeRepo coreThemeRepo;

	@Autowired
	private ThemePropertyRepo themePropertyRepo;
	
	@Autowired 
	ThemePropertyNameRepo themePropertyNameRepo;

	@Override
	public CoreTheme create(String name) {
		CoreTheme theme = coreThemeRepo.getByName(name);
		if(theme == null) {
			CoreTheme newTheme = new CoreTheme(name);
			coreThemeRepo.save(newTheme);
			themePropertyNameRepo.findAll().forEach(tp -> {
				ThemeProperty newProperty = themePropertyRepo.create(tp,null);
				this.addThemeProperty(newTheme, newProperty);
				themePropertyRepo.save(newProperty);
			});
			return coreThemeRepo.save(newTheme);
		}
		return theme;
	}
	
	public void updateActiveTheme(CoreTheme theme) {
		CoreTheme activeTheme = coreThemeRepo.findByActiveTrue();
		if (activeTheme != null) {
			activeTheme.setActive(false);
			coreThemeRepo.save(activeTheme);
		}
		theme.setActive(true);
		coreThemeRepo.save(theme);
	}

	public void addThemeProperty(CoreTheme theme,ThemeProperty themeProperty) {
		theme.addProperty(themeProperty);
		themeProperty.setTheme(theme);
		themePropertyRepo.save(themeProperty);
    	coreThemeRepo.save(theme);
	}
	
	public void updateThemeProperty(Long themeId,Long themePropertyId,String value) {
		CoreTheme theme = coreThemeRepo.getById(themeId);
		ThemeProperty themeProperty = themePropertyRepo.getThemePropertyById(themePropertyId);
		themeProperty.setValue(value);
		Optional<ThemeProperty> existingThemeProperty = theme.getProperties().stream().filter(tp -> tp.getId() == themePropertyId).findFirst();
		if (existingThemeProperty.isPresent()) {
			existingThemeProperty.get().setValue(value);
		}
		themePropertyRepo.save(themeProperty);
		coreThemeRepo.save(theme);
	}
	
}
