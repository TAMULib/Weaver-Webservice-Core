package edu.tamu.weaver.wro.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.wro.model.CoreTheme;
import edu.tamu.weaver.wro.model.ThemeProperty;
import edu.tamu.weaver.wro.model.ThemePropertyName;
import edu.tamu.weaver.wro.model.repo.CoreThemeRepo;
import edu.tamu.weaver.wro.model.repo.ThemePropertyNameRepo;
import edu.tamu.weaver.wro.model.repo.ThemePropertyRepo;

@Component
public class RepoThemeManagerService extends SimpleThemeManagerService {

    @Autowired
    private CoreThemeRepo coreThemeRepo;

    @Autowired
    private ThemePropertyNameRepo themePropertyNameRepo;

    @Autowired
    private ThemePropertyRepo themePropertyRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private CoreTheme currentTheme;

    @Value("${theme.manager:false}")
    private Boolean useThemeManager;

    @Value("${theme.defaults.location:''}")
    private String themeDefaultsFile;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RepoThemeManagerService() {
    }

    @PostConstruct
    public void setUp() {
        if (useThemeManager) {
            if (coreThemeRepo.count() == 0 && !themeDefaultsFile.equals("")) {
                logger.debug("Prepping Defaults :" + coreThemeRepo.count() + "");
                ClassPathResource themeDefaultsRaw = new ClassPathResource(themeDefaultsFile);
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
                CoreTheme defaultTheme = coreThemeRepo.findOne(activateId);
                this.setCurrentTheme(defaultTheme);
            } else {
                this.setCurrentTheme(coreThemeRepo.findByActiveTrue());
            }
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

    public String getFormattedProperties() {
        StringBuilder formattedProperties = new StringBuilder();
        StringBuilder formattedComments = new StringBuilder();
        formattedComments.append("/* The ThemeManagerService added the following SASS vars:\n\n");
        if (this.getCurrentTheme() != null) {
            for (ThemeProperty p : this.getCurrentTheme().getThemeProperties()) {
                formattedProperties.append("$" + p.getThemePropertyName().getName() + ": " + p.getValue() + ";\n");
                formattedComments.append("* $" + p.getThemePropertyName().getName() + ": " + p.getValue() + ";\n");
            }
            formattedComments.append("*/\n\n");
            return formattedComments + formattedProperties.toString();
        }
        return formattedComments.toString() + " n/a\n*/\n";
    }

    public void setCurrentTheme(CoreTheme theme) {
        Boolean hadTheme = (this.currentTheme != null) ? true : false;
        this.currentTheme = theme;
        coreThemeRepo.updateActiveTheme(theme);
        if (hadTheme) {
            this.reloadCache();
        }
    }

}
