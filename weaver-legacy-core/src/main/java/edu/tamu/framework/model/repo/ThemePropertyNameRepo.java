/* 
 * ThemePropertyNameRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.weaver.model.ThemePropertyName;
import edu.tamu.weaver.model.repo.custom.ThemePropertyNameRepoCustom;

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
