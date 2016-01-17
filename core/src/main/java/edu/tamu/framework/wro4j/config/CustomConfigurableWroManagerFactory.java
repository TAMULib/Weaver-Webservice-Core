package edu.tamu.framework.wro4j.config;

import java.util.Map;
import java.util.Properties;

import edu.tamu.framework.service.ThemeManagerService;
import edu.tamu.framework.wro4j.locators.CustomUriLocator;
import edu.tamu.framework.wro4j.processors.RepoPostProcessor;
import ro.isdc.wro.model.resource.locator.ClasspathUriLocator;
import ro.isdc.wro.model.resource.locator.ServletContextUriLocator;
import ro.isdc.wro.model.resource.locator.UrlUriLocator;
import ro.isdc.wro.model.resource.locator.factory.SimpleUriLocatorFactory;
import ro.isdc.wro.model.resource.locator.factory.UriLocatorFactory;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import wro4jBoot.Wro4jCustomXmlModelManagerFactory;

public class CustomConfigurableWroManagerFactory extends Wro4jCustomXmlModelManagerFactory {
	private ThemeManagerService themeManagerService;
	
	public CustomConfigurableWroManagerFactory(Properties props,ThemeManagerService themeManagerService) {
		super(props);
		this.themeManagerService = themeManagerService;
	}
	
	@Override
	protected void contributePostProcessors(Map<String, ResourcePostProcessor> map) {
		map.put("repoPostProcessor", new RepoPostProcessor());
	}
	
	protected UriLocatorFactory newUriLocatorFactory() {
		return new SimpleUriLocatorFactory().addLocator(new ServletContextUriLocator()).addLocator(new ClasspathUriLocator()).addLocator(
				new UrlUriLocator()).addLocator(new CustomUriLocator(themeManagerService));
	 }

}
