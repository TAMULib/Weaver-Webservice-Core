package edu.tamu.weaver.wro.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;

import edu.tamu.weaver.wro.manager.factory.WeaverConfigurableWroManagerFactory;
import edu.tamu.weaver.wro.service.ThemeManager;
import ro.isdc.wro.config.support.ConfigConstants;
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

    private ThemeManager themeManagerService;

    private ResourcePatternResolver resourcePatternResolver;

    @Value("${theme.managerService:edu.tamu.weaver.wro.service.SimpleThemeManagerService}")
    private String themeManagerServiceClassName;

    @Value("${theme.cssGroupName:app}")
    private String cssGroupName;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public FilterRegistrationBean<ConfigurableWroFilter> webResourceOptimizer(Environment env) {
        FilterRegistrationBean<ConfigurableWroFilter> fr = new FilterRegistrationBean<>();
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

    @Bean
    public ThemeManager setThemeManagerServiceBean() {
        logger.debug("Creating ThemeManagerService Bean with configured class: "+themeManagerServiceClassName);
        Class<?> clazz;
        try {
            clazz = Class.forName(themeManagerServiceClassName);
            Constructor<?> constructor;
            try {
                constructor = clazz.getConstructor();
                return (ThemeManager) constructor.newInstance();
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                logger.error("Could not create ThemeManagerService Bean with class: "+themeManagerServiceClassName, e);
            }
        } catch (ClassNotFoundException e) {
            logger.warn("Could not find ThemeManagerService implementation class: " + themeManagerServiceClassName + "! Application must create theme manager bean!");
        }
        return null;
    }

    protected Properties buildWroProperties(Environment env) {
        Properties props = buildDefaultWroProperties(env);
        return props;
    }

    private Properties buildDefaultWroProperties(Environment env) {
        Properties prop = new Properties();
        for (ConfigConstants c : ConfigConstants.values()) {
            addProperty(env, prop, c.name());
        }
        for (String name : OTHER_WRO_PROP) {
            addProperty(env, prop, name);
        }
        return prop;
    }

    protected void addProperty(Environment env, Properties to, String name) {
        String value = env.getProperty(getPropertyPrefix() + "." + name);
        if (value != null) {
            to.put(name, value);
        }
    }

    @Autowired
    @Lazy
    public void setThemeManagerService(ThemeManager themeManagerService) {
        this.themeManagerService = themeManagerService;
    }

    public ThemeManager getThemeManagerService() {
        return themeManagerService;
    }

    @Lazy
    @Autowired
    protected void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public ResourcePatternResolver getResourcePatternResolver() {
      return resourcePatternResolver;
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

    protected String getCssGroupName() {
        return cssGroupName;
    }

    protected WroManagerFactory getWroManagerFactory(Properties properties) {
        return new WeaverConfigurableWroManagerFactory(properties, getThemeManagerService(), resourcePatternResolver, getCssGroupName());
    }

    protected RequestHandler getRequestHandler() {
        return new WeaverRequestHandler();
    }
}
