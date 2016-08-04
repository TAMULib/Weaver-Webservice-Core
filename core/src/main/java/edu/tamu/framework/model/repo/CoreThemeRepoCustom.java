package edu.tamu.framework.model.repo;

import edu.tamu.framework.model.CoreTheme;
import edu.tamu.framework.model.ThemeProperty;

public interface CoreThemeRepoCustom {
    
    public CoreTheme addThemeProperty(CoreTheme theme,ThemeProperty themeProperty);
    
    public void updateActiveTheme(CoreTheme theme);
    
    public void updateThemeProperty(Long themeId,Long themePropertyId,String value);
	
	public CoreTheme create(String name);
	
	public void delete(CoreTheme theme);

}
