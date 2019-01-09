package edu.tamu.weaver.wro.model.repo.impl;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.wro.model.ThemeProperty;
import edu.tamu.weaver.wro.model.ThemePropertyName;
import edu.tamu.weaver.wro.model.repo.ThemePropertyRepo;
import edu.tamu.weaver.wro.model.repo.custom.ThemePropertyRepoCustom;

public class ThemePropertyRepoImpl extends AbstractWeaverRepoImpl<ThemeProperty, ThemePropertyRepo> implements ThemePropertyRepoCustom {

    @Override
    public ThemeProperty create(ThemePropertyName propertyName, String value) {
        return super.create(new ThemeProperty(propertyName, value));
    }
    
    @Override
    protected String getChannel() {
        return "/channel/theme-property";
    }

}
