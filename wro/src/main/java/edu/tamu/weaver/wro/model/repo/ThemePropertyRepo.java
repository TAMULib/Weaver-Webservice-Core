package edu.tamu.weaver.wro.model.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import edu.tamu.weaver.wro.model.ThemeProperty;
import edu.tamu.weaver.wro.model.ThemePropertyName;
import edu.tamu.weaver.wro.model.repo.custom.ThemePropertyRepoCustom;

/**
 * Application User repository.
 * 
 * @author
 *
 */
@Repository
public interface ThemePropertyRepo extends WeaverRepo<ThemeProperty>, ThemePropertyRepoCustom {

    public ThemeProperty findThemePropertyByThemePropertyNameAndThemeId(ThemePropertyName propertyName, Long themeId);

    public ThemeProperty findThemePropertyById(Long id);

    public List<ThemeProperty> findThemePropertyByThemePropertyName(ThemePropertyName themePropertyName);

}
