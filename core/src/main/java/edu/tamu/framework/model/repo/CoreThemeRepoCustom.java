package edu.tamu.framework.model.repo;

import edu.tamu.framework.model.CoreTheme;

public interface CoreThemeRepoCustom {
    
    public void updateActiveTheme(CoreTheme theme);
    
    public void updateThemeProperty(Long themeId,Long themePropertyId,String value);
	
	public CoreTheme create(String name);
	
	public void delete(CoreTheme theme);

}
