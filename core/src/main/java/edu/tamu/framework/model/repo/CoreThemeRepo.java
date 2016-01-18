/* 
 * CoreThemeRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.framework.model.CoreTheme;
import edu.tamu.framework.model.ThemeProperty;

/**
 * Application User repository.
 * 
 * @author
 *
 */
@Repository
public interface CoreThemeRepo extends JpaRepository<CoreTheme, Long>, CoreThemeRepoCustom {
	
	public CoreTheme getByName(String name);
	
	public CoreTheme findByActiveTrue();

	public void updateActiveTheme(CoreTheme theme);
	
	public void addThemeProperty(CoreTheme theme,ThemeProperty themeProperty);

}
