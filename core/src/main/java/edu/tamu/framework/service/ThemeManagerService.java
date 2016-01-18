package edu.tamu.framework.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.tamu.framework.model.CoreTheme;
import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;
import edu.tamu.framework.model.repo.CoreThemeRepo;
import edu.tamu.framework.model.repo.ThemePropertyNameRepo;
import edu.tamu.framework.model.repo.ThemePropertyRepo;

@Component
public class ThemeManagerService {
	@Autowired
	private CoreThemeRepo coreThemeRepo;
	
	@Autowired
	private ThemePropertyNameRepo themePropertyNameRepo;

	@Autowired
	private ThemePropertyRepo themePropertyRepo;

	private CoreTheme currentTheme;
	
	public ThemeManagerService() {}
	
	@PostConstruct
	public void goNow() {
		//TODO Make the defaults configurable and initially loaded in a better way
		if (coreThemeRepo.count() < 1) {
			CoreTheme defaultTheme = coreThemeRepo.create("Default");
			if (themePropertyNameRepo.count() < 1) {
				Map<ThemePropertyName,String> newProperties = new HashMap<ThemePropertyName,String>();
				newProperties.put(themePropertyNameRepo.create("primary"), "#500000");
				newProperties.put(themePropertyNameRepo.create("secondary"), "#dfdfdf");
				newProperties.put(themePropertyNameRepo.create("baseFontSize"), "14pt");
				newProperties.put(themePropertyNameRepo.create("linkColor"), "#337ab7");

				newProperties.forEach((propertyName,defaultValue) -> {
					ThemeProperty themeProperty = themePropertyRepo.create(propertyName,defaultValue);
					defaultTheme.addProperty(themeProperty);
					coreThemeRepo.addThemeProperty(defaultTheme,themeProperty);
				});
			}
			defaultTheme.setActive(true);
			coreThemeRepo.save(defaultTheme);
			currentTheme = defaultTheme;
		}
	}
	
	public CoreTheme getCurrentTheme() {
		return currentTheme;
	}
	
	public String getFormattedProperties() {
		StringBuilder formattedProperties = new StringBuilder();
		StringBuilder formattedComments = new StringBuilder();
		formattedComments.append("/* The ThemeManagerService added the following SASS vars:\n\n");
		for (ThemeProperty p : this.getCurrentTheme().getProperties()) {
			formattedProperties.append("$"+p.getPropertyName().getName()+": "+p.getValue()+";\n");
			formattedComments.append("* $"+p.getPropertyName().getName()+": "+p.getValue()+";\n");
		}
		formattedComments.append("*/\n\n");
		return formattedComments+formattedProperties.toString();
	}

	public void setCurrentTheme(CoreTheme theme) {
		coreThemeRepo.updateActiveTheme(theme);
		this.currentTheme = theme;
	}
}
