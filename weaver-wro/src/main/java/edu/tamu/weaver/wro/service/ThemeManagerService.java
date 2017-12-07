package edu.tamu.weaver.wro.service;

import edu.tamu.weaver.wro.model.CoreTheme;

public interface ThemeManagerService {
	public void setUp();
	public CoreTheme getCurrentTheme();
	public void refreshCurrentTheme();
	public String getFormattedProperties();
	public String[] getCssResources();
}
