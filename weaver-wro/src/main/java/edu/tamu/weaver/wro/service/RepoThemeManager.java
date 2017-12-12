package edu.tamu.weaver.wro.service;

import edu.tamu.weaver.wro.model.CoreTheme;

public interface RepoThemeManager extends ThemeManager {
    /*
     * Get the current active theme
     * @return CoreTheme
     */
    public CoreTheme getCurrentTheme();
}
