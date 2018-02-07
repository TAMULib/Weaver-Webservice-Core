package edu.tamu.weaver.wro4j.model.factory;

import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

public class CustomWroModelFactory implements WroModelFactory {

    private String[] defaultResources;

    public CustomWroModelFactory(String[] defaultResources) {
        this.defaultResources = defaultResources;
    }

    public WroModel create() {
        Group cssGroup = new Group("app");
        for (String cssResource : defaultResources) {
            cssGroup.addResource(Resource.create(cssResource, ResourceType.CSS));
        }
        return new WroModel().addGroup(cssGroup);
    }

    @Override
    public void destroy() {

    }

}
