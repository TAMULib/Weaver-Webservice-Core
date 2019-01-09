package edu.tamu.weaver.wro.model.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import edu.tamu.weaver.wro.model.CoreTheme;
import edu.tamu.weaver.wro.model.repo.custom.CoreThemeRepoCustom;

/**
 * Application User repository.
 * 
 * @author
 *
 */
@Repository
public interface CoreThemeRepo extends WeaverRepo<CoreTheme>, CoreThemeRepoCustom {

    public CoreTheme getByName(String name);

    public CoreTheme getById(Long id);

    public CoreTheme findByActiveTrue();

    public List<CoreTheme> findByThemePropertiesId(Long propertiesId);

}
