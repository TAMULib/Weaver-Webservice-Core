package edu.tamu.framework.wro4j.manager.factory;

import java.util.Map;
import java.util.Properties;

import edu.tamu.framework.service.ThemeManagerService;
import edu.tamu.framework.wro4j.model.factory.CustomWroModelFactory;
import edu.tamu.framework.wro4j.processors.RepoPostProcessor;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;

public class CustomConfigurableWroManagerFactory extends ConfigurableWroManagerFactory {
	private ThemeManagerService themeManagerService;
	
	final private Properties props;

	public CustomConfigurableWroManagerFactory(Properties props,ThemeManagerService themeManagerService) {
		this.props = props;
		this.themeManagerService = themeManagerService;
	}

	@Override
	protected Properties newConfigProperties() {
		return props;
	}
	
	@Override
	protected void contributePostProcessors(Map<String, ResourcePostProcessor> map) {
		map.put("repoPostProcessor", new RepoPostProcessor(themeManagerService));
	}
	
	@Override
	protected WroModelFactory newModelFactory() {
		return new CustomWroModelFactory();
  }

}
