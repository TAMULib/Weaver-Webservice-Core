/* 
 * ThemePropertyRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.weaver.model.ThemeProperty;
import edu.tamu.weaver.model.ThemePropertyName;
import edu.tamu.weaver.model.repo.custom.ThemePropertyRepoCustom;

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
