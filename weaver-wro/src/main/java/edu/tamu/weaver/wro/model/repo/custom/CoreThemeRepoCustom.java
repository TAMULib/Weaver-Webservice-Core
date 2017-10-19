package edu.tamu.weaver.wro.model.repo.custom;

import edu.tamu.weaver.wro.model.CoreTheme;
import edu.tamu.weaver.wro.model.ThemeProperty;

public interface CoreThemeRepoCustom {

    public CoreTheme addThemeProperty(CoreTheme theme, ThemeProperty themeProperty);

    public void updateActiveTheme(CoreTheme theme);

    public void updateThemeProperty(Long themeId, Long themePropertyId, String value);

    public CoreTheme create(String name);

}
