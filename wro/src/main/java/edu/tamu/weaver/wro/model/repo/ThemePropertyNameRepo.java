package edu.tamu.weaver.wro.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import edu.tamu.weaver.wro.model.ThemePropertyName;
import edu.tamu.weaver.wro.model.repo.custom.ThemePropertyNameRepoCustom;

/**
 * Application User repository.
 * 
 * @author
 *
 */
@Repository
public interface ThemePropertyNameRepo extends WeaverRepo<ThemePropertyName>, ThemePropertyNameRepoCustom {

    public ThemePropertyName getThemePropertyNameByName(String name);

}
