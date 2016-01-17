package edu.tamu.framework.model.repo;

import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;

public interface ThemePropertyRepoCustom {
	
	public ThemeProperty create(ThemePropertyName propertyName,String value);

}
