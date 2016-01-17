package edu.tamu.framework.wro4j.model.factory;

import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

public class CustomWroModelFactory implements WroModelFactory {
	  public WroModel create() {
		  //TODO tap ThemeManagerService for dynamic resources
		  return new WroModel().addGroup(new Group("app").addResource(
				  		Resource.create("http://savell.evans.tamu.edu/tamu-ui-seed/bower_components/core/app/resources/styles/sass/main.scss", ResourceType.CSS)).addResource(
				  		Resource.create("http://savell.evans.tamu.edu/tamu-ui-seed/resources/styles/sass/app.scss", ResourceType.CSS)));
	  }

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
