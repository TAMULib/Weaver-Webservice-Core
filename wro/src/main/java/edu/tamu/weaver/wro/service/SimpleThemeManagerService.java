package edu.tamu.weaver.wro.service;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import edu.tamu.weaver.utility.HttpUtility;

public class SimpleThemeManagerService implements ThemeManager {
    @Value("${theme.cacheReloadUrl:http://localhost:9000/wro/wroAPI/reloadCache}")
    private String cacheReloadUrl;

    @Value("${theme.default.css:''}")
    private String[] defaultCssGroup;

    @Value("${theme.cssUrl:http://localhost:9000/wro/app.css}")
    protected String cssUrl;

    @Value("${theme.initialize:true}")
    protected Boolean initializeTheme;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    public String[] getCssResources() {
        return this.defaultCssGroup;
    }

    @Override
    public Map<String, String> getThemeProperties() {
        return null;
    }

    /**
     * Build the CSS as soon as the the app is running
     *
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeResources() {
        if (initializeTheme) {
            new Thread(new Runnable() {
                @Override
                public void run()  {
                    logger.debug("Initializing theme...");
                    try {
                        HttpUtility.makeHttpRequest(cssUrl, "GET");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
