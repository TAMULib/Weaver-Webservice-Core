package edu.tamu.weaver.wro.model.factory;

import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

public class WeaverWroModelFactory implements WroModelFactory {

	private String[] defaultResources;
	private String groupName = "app";

	public WeaverWroModelFactory(String[] defaultResources) {
		setDefaultResources(defaultResources);
	}

	public WroModel create() {
		Group cssGroup = new Group(getGroupName());
		for (String cssResource : getDefaultResources()) {
			cssGroup.addResource(Resource.create(cssResource, ResourceType.CSS));
		}
		return new WroModel().addGroup(cssGroup);
	}

	@Override
	public void destroy() {

	}
	
	protected void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	protected String getGroupName() {
		return groupName;
	}
	
	protected void setDefaultResources(String[] defaultResources) {
		this.defaultResources = defaultResources;
	}
	
	protected String[] getDefaultResources() {
		return defaultResources;
	}

}