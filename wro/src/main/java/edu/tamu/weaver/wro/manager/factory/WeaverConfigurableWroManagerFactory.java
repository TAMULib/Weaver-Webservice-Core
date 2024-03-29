package edu.tamu.weaver.wro.manager.factory;

import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.support.ResourcePatternResolver;

import edu.tamu.weaver.wro.resource.locator.SassClassPathUriLocator;
import edu.tamu.weaver.wro.model.factory.WeaverWroModelFactory;
import edu.tamu.weaver.wro.processor.RepoPostProcessor;
import edu.tamu.weaver.wro.service.ThemeManager;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.locator.ClasspathUriLocator;
import ro.isdc.wro.model.resource.locator.ServletContextUriLocator;
import ro.isdc.wro.model.resource.locator.UrlUriLocator;
import ro.isdc.wro.model.resource.locator.factory.SimpleUriLocatorFactory;
import ro.isdc.wro.model.resource.locator.factory.UriLocatorFactory;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

public class WeaverConfigurableWroManagerFactory extends ConfigurableWroManagerFactory {

	private ThemeManager themeManagerService;

	private Properties properties;

	private String cssGroupName;
	
	private ResourcePatternResolver resourcePatternResolver;

	private static final String DEFAULT_CSS_GROUP_NAME = "app";

	public WeaverConfigurableWroManagerFactory(Properties props, ThemeManager themeManagerService, ResourcePatternResolver resourcePatternResolver) {
		this(props, themeManagerService, resourcePatternResolver, DEFAULT_CSS_GROUP_NAME);
	}

	public WeaverConfigurableWroManagerFactory(Properties props, ThemeManager themeManagerService, ResourcePatternResolver resourcePatternResolver, String cssGroupName) {
		setProperties(props);
		setThemeManagerService(themeManagerService);
		setCssGroupName(cssGroupName);
		this.resourcePatternResolver = resourcePatternResolver;
	}

	@Override
	protected Properties newConfigProperties() {
		return properties;
	}

	@Override
	protected void contributePostProcessors(Map<String, ResourcePostProcessor> map) {
	    map.put("repoPostProcessor", new RepoPostProcessor(getThemeManagerService()));
	}

	@Override
	protected WroModelFactory newModelFactory() {
		return new WeaverWroModelFactory(getThemeManagerService().getCssResources(), getCssGroupName());
	}
	
	protected void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	protected void setThemeManagerService(ThemeManager themeManagerService) {
		this.themeManagerService = themeManagerService;
	}

	protected ThemeManager getThemeManagerService() {
		return themeManagerService;
	}

    protected void setCssGroupName(String cssGroupName) {
		this.cssGroupName = cssGroupName;
	}

    protected String getCssGroupName() {
		return cssGroupName;
	}
	
	protected UriLocatorFactory newUriLocatorFactory() {
		return new SimpleUriLocatorFactory().addLocator(new SassClassPathUriLocator(resourcePatternResolver)).addLocator(new ServletContextUriLocator()).addLocator(new ClasspathUriLocator()).addLocator(new UrlUriLocator());
	}
}