package edu.tamu.weaver.wro.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private ObjectMapper objectMapper;

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
                themeDefaults = objectMapper.readTree(new FileInputStream(themeDefaultsRaw.getFile()));
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Iterator<JsonNode> itProps = themeDefaults.get("propertyNames").elements();
            while (itProps.hasNext()) {
                JsonNode entry = itProps.next();
                logger.debug("Creating Theme Property: " + entry.textValue() + "");
                themePropertyNameRepo.create(entry.textValue());
            }

            Iterator<Entry<String, JsonNode>> it = themeDefaults.get("themes").fields();
            Long activateId = 0L;
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) it.next();
                if (entry.getValue().isArray()) {
                    logger.debug("New Props for: " + entry.getKey());
                    if (coreThemeRepo.getByName(entry.getKey()) == null) {
                        CoreTheme newTheme = coreThemeRepo.create(entry.getKey());
                        if (activateId == 0) {
                            activateId = newTheme.getId();
                        }
                        JsonNode defaultProperties = entry.getValue();
                        for (ThemePropertyName propertyName : themePropertyNameRepo.findAll()) {
                            String value = defaultProperties.findValue(propertyName.getName()).asText();
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
