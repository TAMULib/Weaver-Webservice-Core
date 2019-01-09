package edu.tamu.weaver.wro.model.repo.custom;

import edu.tamu.weaver.wro.model.ThemeProperty;
import edu.tamu.weaver.wro.model.ThemePropertyName;

public interface ThemePropertyRepoCustom {

    public ThemeProperty create(ThemePropertyName propertyName, String value);

}
