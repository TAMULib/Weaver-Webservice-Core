package edu.tamu.weaver.wro.model.repo.impl;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.wro.model.ThemePropertyName;
import edu.tamu.weaver.wro.model.repo.ThemePropertyNameRepo;

public class ThemePropertyNameRepoImpl extends AbstractWeaverRepoImpl<ThemePropertyName, ThemePropertyNameRepo> {

    public ThemePropertyName create(String name) {
        return super.create(new ThemePropertyName(name));
    }
    
    @Override
    protected String getChannel() {
        return "/channel/theme/property/name";
    }

}
