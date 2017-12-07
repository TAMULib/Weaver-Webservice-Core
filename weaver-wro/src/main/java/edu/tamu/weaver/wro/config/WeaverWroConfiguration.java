package edu.tamu.weaver.wro.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;

import edu.tamu.weaver.wro.manager.factory.WeaverConfigurableWroManagerFactory;
import edu.tamu.weaver.wro.service.ThemeManagerService;
import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.handler.RequestHandler;
import ro.isdc.wro.http.handler.factory.SimpleRequestHandlerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import wro4j.http.handler.WeaverRequestHandler;

@Configuration
public class WeaverWroConfiguration {
    private static final String[] OTHER_WRO_PROP = new String[] { ConfigurableProcessorsFactory.PARAM_PRE_PROCESSORS, ConfigurableProcessorsFactory.PARAM_POST_PROCESSORS };
    private String propertyPrefix = "wro";
    private String wroEndpoint = propertyPrefix;
    
    private ThemeManagerService themeManagerService;
    
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;
    
    @Bean
    public FilterRegistrationBean webResourceOptimizer(Environment env) {
        FilterRegistrationBean fr = new FilterRegistrationBean();
        ConfigurableWroFilter filter = new ConfigurableWroFilter();
        Properties props = buildWroProperties(env);
        filter.setProperties(props);
        filter.setWroManagerFactory(getWroManagerFactory(props));
        filter.setRequestHandlerFactory(new SimpleRequestHandlerFactory().addHandler(getRequestHandler()));
        filter.setProperties(props);
        fr.setFilter(filter);
        fr.addUrlPatterns("/"+getWroEndpoint()+"/*");
        return fr;
    }

    protected Properties buildWroProperties(Environment env) {
        Properties prop = new Properties();
        for (ConfigConstants c : ConfigConstants.values()) {
            addProperty(env, prop, c.name());
        }
        for (String name : OTHER_WRO_PROP) {
            addProperty(env, prop, name);
        }
        return prop;
    }

    private void addProperty(Environment env, Properties to, String name) {
        String value = env.getProperty(getPropertyPrefix() + "." + name);
        if (value != null) {
            to.put(name, value);
        }
    }
    
    @Lazy
    @Autowired
    protected void setThemeManagerService(ThemeManagerService themeManagerService) {
    	this.themeManagerService = themeManagerService;
    }
    
    public ThemeManagerService getThemeManagerService() {
    	return themeManagerService;
    }
    
    protected void setPropertyPrefix(String propertyPrefix) {
    	this.propertyPrefix = propertyPrefix;
    }
    
    protected String getPropertyPrefix() {
    	return propertyPrefix;
    }
    
    protected void setWroEndpoint(String wroEndpoint) {
    	this.wroEndpoint = wroEndpoint;
    }
    
    protected String getWroEndpoint() {
    	return wroEndpoint;
    }
    
    protected WroManagerFactory getWroManagerFactory(Properties properties) {
    	return new WeaverConfigurableWroManagerFactory(properties, getThemeManagerService(), resourcePatternResolver);
    }
    
    protected RequestHandler getRequestHandler() {
    	return new WeaverRequestHandler();
    }

}
