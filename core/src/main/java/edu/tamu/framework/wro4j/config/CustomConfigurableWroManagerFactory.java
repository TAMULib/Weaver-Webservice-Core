package edu.tamu.framework.wro4j.config;

import java.util.Map;
import java.util.Properties;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import wro4jBoot.Wro4jCustomXmlModelManagerFactory;
import edu.tamu.framework.wro4j.processors.RepoPostProcessor;

public class CustomConfigurableWroManagerFactory extends Wro4jCustomXmlModelManagerFactory {
	public CustomConfigurableWroManagerFactory(Properties props) {
		super(props);
	}
	
	@Override
	protected void contributePostProcessors(Map<String, ResourcePostProcessor> map) {
		map.put("repoPostProcessor", new RepoPostProcessor());
	}

}
