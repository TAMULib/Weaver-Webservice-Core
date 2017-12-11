package edu.tamu.weaver.wro.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import edu.tamu.weaver.utility.HttpUtility;
import edu.tamu.weaver.wro.model.CoreTheme;

public class SimpleThemeManagerService implements ThemeManager {
    @Value("${theme.cacheReloadUrl:http://localhost:9000/wro/wroAPI/reloadCache}")
    private String cacheReloadUrl;
    
    @Value("${theme.default.css:''}")
    private String[] defaultCssGroup;

	@Override
	public void setUp() {
		
	}

	@Override
	public CoreTheme getCurrentTheme() {
		return null;
	}

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

	@Override
	public String getFormattedProperties() {
		return "";
	}

    public String[] getCssResources() {
        return this.defaultCssGroup;
    }

}
