package edu.tamu.weaver.wro.service;

import java.util.Map;

public interface ThemeManager {
    /*
     * Get a map of all theme properties (name -> value)
     * @return Map<String,String>
     */
    public Map<String,String> getThemeProperties();

    /*
     * Trigger a re-processing of all theme resources
     */
    public void refreshCurrentTheme();

    /*
     * Get an array of all the CSS derivable file locations
     * @return String[] A string array of file locations
     */
    public String[] getCssResources();
}
