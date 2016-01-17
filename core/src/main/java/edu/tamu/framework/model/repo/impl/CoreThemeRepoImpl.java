package edu.tamu.framework.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.framework.model.CoreTheme;
import edu.tamu.framework.model.repo.CoreThemeRepo;
import edu.tamu.framework.model.repo.CoreThemeRepoCustom;

public class CoreThemeRepoImpl implements CoreThemeRepoCustom {
	
	@Autowired
	private CoreThemeRepo coreThemeRepo;

	@Override
	public CoreTheme create(String name) {
		CoreTheme theme = coreThemeRepo.getByName(name);
		if(theme == null) {
			return coreThemeRepo.save(new CoreTheme(name));
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

}
