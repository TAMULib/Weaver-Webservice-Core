package edu.tamu.framework.model.repo.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;
import edu.tamu.framework.model.repo.CoreThemeRepo;
import edu.tamu.framework.model.repo.ThemePropertyRepo;
import edu.tamu.framework.model.repo.ThemePropertyRepoCustom;
import edu.tamu.framework.service.ThemeManagerService;

public class ThemePropertyRepoImpl implements ThemePropertyRepoCustom {
	
	@Autowired
	private ThemePropertyRepo themePropertyRepo;
	
	private static final Logger logger = Logger.getLogger(ThemePropertyRepoImpl.class);

	@Override
	public ThemeProperty create(ThemePropertyName propertyName, String value) {
		logger.debug("\n\n\n"+propertyName+" has a value of: "+value);
		return themePropertyRepo.save(new ThemeProperty(propertyName,value));
	}

}
