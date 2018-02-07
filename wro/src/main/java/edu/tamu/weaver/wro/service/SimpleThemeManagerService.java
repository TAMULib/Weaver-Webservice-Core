package edu.tamu.weaver.wro.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import edu.tamu.weaver.utility.HttpUtility;

public class SimpleThemeManagerService implements ThemeManager {
    @Value("${theme.cacheReloadUrl:http://localhost:9000/wro/wroAPI/reloadCache}")
    private String cacheReloadUrl;
    
    @Value("${theme.default.css:''}")
    private String[] defaultCssGroup;

	@Override
	public void refreshCurrentTheme() {
        this.reloadCache();
    }

    // tell WRO to reset its resource cache
    protected void reloadCache() {
        try {
            HttpUtility.makeHttpRequest(cacheReloadUrl, "GET");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getCssResources() {
        return this.defaultCssGroup;
    }

    @Override
    public Map<String, String> getThemeProperties() {
        return null;
    }
}
