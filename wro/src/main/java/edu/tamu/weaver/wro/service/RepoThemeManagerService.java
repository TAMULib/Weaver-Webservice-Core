package edu.tamu.weaver.wro.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import edu.tamu.weaver.wro.model.CoreTheme;
import edu.tamu.weaver.wro.model.ThemeProperty;
import edu.tamu.weaver.wro.model.ThemePropertyName;
import edu.tamu.weaver.wro.model.repo.CoreThemeRepo;
import edu.tamu.weaver.wro.model.repo.ThemePropertyNameRepo;
import edu.tamu.weaver.wro.model.repo.ThemePropertyRepo;

@Service
public class RepoThemeManagerService extends SimpleThemeManagerService implements RepoThemeManager {

    @Autowired
    private CoreThemeRepo coreThemeRepo;

    @Autowired
    private ThemePropertyNameRepo themePropertyNameRepo;

    @Autowired
    private ThemePropertyRepo themePropertyRepo;

    @Autowired
    private JsonMapper jsonMapper;

    private CoreTheme currentTheme;

    @Value("${theme.default.location:''}")
    private String themeDefaultFile;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RepoThemeManagerService() {
    }

    @PostConstruct
    public void setUp() {
        if (coreThemeRepo.count() == 0 && !themeDefaultFile.equals("")) {
            logger.debug("Prepping Defaults :" + coreThemeRepo.count() + "");
            ClassPathResource themeDefaultsRaw = new ClassPathResource(themeDefaultFile);
            JsonNode themeDefaults = null;
            try {
                themeDefaults = jsonMapper.readTree(new FileInputStream(themeDefaultsRaw.getFile()));
            } catch (JacksonException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            Collection<JsonNode> propertyNames = themeDefaults.get("propertyNames").values();
            for (JsonNode propertyName : propertyNames) {
                logger.debug("Creating Theme Property: " + propertyName.stringValue() + "");
                themePropertyNameRepo.create(propertyName.stringValue());
            }

            Set<Entry<String, JsonNode>> fieldSet = themeDefaults.get("themes").properties();
            Long activateId = 0L;
            for (Entry<String, JsonNode> fieldEntry : fieldSet) {
                Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) fieldEntry;
                if (entry.getValue().isArray()) {
                    logger.debug("New Props for: " + entry.getKey());
                    if (coreThemeRepo.getByName(entry.getKey()) == null) {
                        CoreTheme newTheme = coreThemeRepo.create(entry.getKey());
                        if (activateId == 0) {
                            activateId = newTheme.getId();
                        }
                        JsonNode defaultProperties = entry.getValue();
                        for (ThemePropertyName propertyName : themePropertyNameRepo.findAll()) {
                            String value = defaultProperties.findValue(propertyName.getName()).asString();
                            if (!value.isEmpty()) {
                                coreThemeRepo.updateThemeProperty(newTheme.getId(), themePropertyRepo.findThemePropertyByThemePropertyNameAndThemeId(propertyName, newTheme.getId()).getId(), value);
                            }
                        }
                    }
                }
            }
            CoreTheme defaultTheme = coreThemeRepo.getById(activateId);
            this.setCurrentTheme(defaultTheme);
        } else {
            this.setCurrentTheme(coreThemeRepo.findByActiveTrue());
        }
    }

    public CoreTheme getCurrentTheme() {
        return currentTheme;
    }

    public void updateThemeProperty(Long themeId, Long propertyId, String value) {
        coreThemeRepo.updateThemeProperty(themeId, propertyId, value);
        // if the updated property is part of the active theme, get it fresh from the repo
        if (this.getCurrentTheme().getId() == themeId) {
            this.refreshCurrentTheme();
        }
    }

    /*
     * Gets a fresh version of the active theme from the repo
     */
    public void refreshCurrentTheme() {
        logger.debug("The properties were:");
        currentTheme.getThemeProperties().forEach(tp -> {
            logger.debug(tp.getThemePropertyName().getName() + ": " + tp.getValue());
        });
        currentTheme = coreThemeRepo.getById(currentTheme.getId());

        logger.debug("The properties are now:");
        currentTheme.getThemeProperties().forEach(tp -> {
            logger.debug(tp.getThemePropertyName().getName() + ": " + tp.getValue());
        });
        reloadCache();
    }

    @Override
    public Map<String,String> getThemeProperties() {
      Map<String,String> themeProperties = new HashMap<String,String>();
        if (this.getCurrentTheme() != null) {
            for (ThemeProperty p : this.getCurrentTheme().getThemeProperties()) {
              themeProperties.put(p.getThemePropertyName().getName(),p.getValue());
            }
        }
        return themeProperties;
    }

    @Override
    public void setCurrentTheme(CoreTheme theme) {
        Boolean hadTheme = (this.currentTheme != null) ? true : false;
        this.currentTheme = theme;
        coreThemeRepo.updateActiveTheme(theme);
        if (hadTheme) {
            this.reloadCache();
        }
    }

}
