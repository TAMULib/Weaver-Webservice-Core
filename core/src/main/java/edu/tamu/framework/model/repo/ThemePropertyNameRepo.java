/* 
 * ThemePropertyNameRepo.java 
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

import edu.tamu.framework.model.ThemePropertyName;

/**
 * Application User repository.
 * 
 * @author
 *
 */
@Repository
public interface ThemePropertyNameRepo extends JpaRepository<ThemePropertyName, Long>, ThemePropertyNameRepoCustom {

    public ThemePropertyName getThemePropertyNameByName(String name);

}
