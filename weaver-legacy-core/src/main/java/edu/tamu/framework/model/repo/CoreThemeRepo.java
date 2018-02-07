/* 
 * CoreThemeRepo.java 
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

import edu.tamu.weaver.model.CoreTheme;
import edu.tamu.weaver.model.repo.custom.CoreThemeRepoCustom;

/**
 * Application User repository.
 * 
 * @author
 *
 */
@Repository
public interface CoreThemeRepo extends JpaRepository<CoreTheme, Long>, CoreThemeRepoCustom {

    public CoreTheme getByName(String name);

    public CoreTheme getById(Long id);

    public CoreTheme findByActiveTrue();

    public List<CoreTheme> findByThemePropertiesId(Long propertiesId);

    @Override
    public void delete(CoreTheme theme);

}
