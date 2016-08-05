/* 
 * ThemePropertyRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;

/**
 * Application User repository.
 * 
 * @author
 *
 */
@Repository
public interface ThemePropertyRepo extends JpaRepository<ThemeProperty, Long>, ThemePropertyRepoCustom {

    public ThemeProperty findThemePropertyByThemePropertyNameAndThemeId(ThemePropertyName propertyName, Long themeId);

    public ThemeProperty findThemePropertyById(Long id);

    public List<ThemeProperty> findThemePropertyByThemePropertyName(ThemePropertyName themePropertyName);

}
