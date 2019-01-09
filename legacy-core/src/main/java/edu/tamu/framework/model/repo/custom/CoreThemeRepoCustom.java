/* 
 * CoreThemeRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.model.repo.custom;

import edu.tamu.weaver.model.CoreTheme;
import edu.tamu.weaver.model.ThemeProperty;

public interface CoreThemeRepoCustom {

    public CoreTheme addThemeProperty(CoreTheme theme, ThemeProperty themeProperty);

    public void updateActiveTheme(CoreTheme theme);

    public void updateThemeProperty(Long themeId, Long themePropertyId, String value);

    public CoreTheme create(String name);

    public void delete(CoreTheme theme);

}
