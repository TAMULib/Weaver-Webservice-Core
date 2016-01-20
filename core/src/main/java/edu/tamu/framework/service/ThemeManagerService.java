package edu.tamu.framework.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.events.StompConnectEvent;
import edu.tamu.framework.model.CoreTheme;
import edu.tamu.framework.model.ThemeProperty;
import edu.tamu.framework.model.ThemePropertyName;
import edu.tamu.framework.model.repo.CoreThemeRepo;
import edu.tamu.framework.model.repo.ThemePropertyNameRepo;
import edu.tamu.framework.model.repo.ThemePropertyRepo;
import edu.tamu.framework.util.HttpUtility;

@Component
public class ThemeManagerService {
	@Autowired
	private CoreThemeRepo coreThemeRepo;
	
	@Autowired
	private ThemePropertyNameRepo themePropertyNameRepo;

	@Autowired
	private ThemePropertyRepo themePropertyRepo;
	
	@Autowired
	private HttpUtility httpUtility;
	
	@Autowired
	private ObjectMapper objectMapper;

	private CoreTheme currentTheme;
	
	@Value("${theme.defaults.location}")
	private String themeDefaultsFile;

	private static final Logger logger = Logger.getLogger(ThemeManagerService.class);

	public ThemeManagerService() {}
	
	@PostConstruct
	public void goNow() {
		//TODO Make the defaults configurable and initially loaded in a better way
		System.out.println("\n\n\nPrepping Defaults\n\n\n");
		if (themePropertyNameRepo.count() < 4) {
			themePropertyNameRepo.create("primary");
			themePropertyNameRepo.create("secondary");
			themePropertyNameRepo.create("baseFontSize");
			themePropertyNameRepo.create("linkColor");
		}
		if (coreThemeRepo.getByName("Default") == null || coreThemeRepo.getByName("Another Theme") == null) {
			ClassPathResource themeDefaultsRaw = new ClassPathResource(themeDefaultsFile); 
//			File themeDefaultsRaw = new File(themeDefaultsFile);
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
			
			Iterator<Entry<String,JsonNode>> it = themeDefaults.fields();
			while (it.hasNext()) {
			    Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) it.next();
			    if (entry.getValue().isArray()) {
		        	logger.debug("\n\nNew Props for: "+entry.getKey());
		        	if (coreThemeRepo.getByName(entry.getKey()) == null) {
		    			CoreTheme newTheme = coreThemeRepo.create(entry.getKey());
				        for (JsonNode objNode : entry.getValue()) {
				            objNode.fieldNames().forEachRemaining(n -> {
//				            	logger.debug(n+" now what has value: "+objNode.get(n).asText());
				            	String value = objNode.get(n).textValue();
				            	if (value != null) {
				            		coreThemeRepo.addThemeProperty(newTheme, themePropertyRepo.create(themePropertyNameRepo.create(n),value));
				            	} else {
				            		logger.debug("\n\n"+n+" was null");
				            	}
			        		});
				        }
			    	}
			    }
			}
			CoreTheme defaultTheme = coreThemeRepo.getByName("Default");
			defaultTheme.setActive(true);
			coreThemeRepo.save(defaultTheme);
/*			
			CoreTheme defaultTheme = coreThemeRepo.create("Default");
			if (themePropertyNameRepo.count() < 1) {
				Map<ThemePropertyName,String> newProperties = new HashMap<ThemePropertyName,String>();
				newProperties.put(themePropertyNameRepo.create("primary"), "#500000");
				newProperties.put(themePropertyNameRepo.create("secondary"), "#3c0000");
				newProperties.put(themePropertyNameRepo.create("baseFontSize"), "14pt");
				newProperties.put(themePropertyNameRepo.create("linkColor"), "#337ab7");

				newProperties.forEach((propertyName,defaultValue) -> {
					ThemeProperty themeProperty = themePropertyRepo.create(propertyName,defaultValue);
					defaultTheme.addProperty(themeProperty);
					coreThemeRepo.addThemeProperty(defaultTheme,themeProperty);
				});
			}
			defaultTheme.setActive(true);
			coreThemeRepo.save(defaultTheme);
			currentTheme = defaultTheme;
			*/
		}
	}
	
	public CoreTheme getCurrentTheme() {
		return currentTheme;
	}
	
	public void updateThemeProperty(Long themeId,Long propertyId,String value) {
		coreThemeRepo.updateThemeProperty(themeId,propertyId,value);
		//if the updated property is part of the active theme, get it fresh from the repo
		if (this.getCurrentTheme().getId() == themeId) {
			this.refreshCurrentTheme();
		}
	}
	
	/*
	 * Gets a fresh version of the active theme from the repo
	 */
	public void refreshCurrentTheme() {
		System.out.println("\n\n\nThe properties were:\n\n");
		currentTheme.getProperties().forEach(tp -> {
			System.out.println(tp.getPropertyName().getName()+": "+tp.getValue());
		});
		currentTheme = coreThemeRepo.getById(currentTheme.getId());
		
		System.out.println("\n\n\nThe properties are now:\n\n");
		currentTheme.getProperties().forEach(tp -> {
			System.out.println(tp.getPropertyName().getName()+": "+tp.getValue());
		});
		this.reloadCache();
	}
	
	//tell WRO to reset its resource cache
	private void reloadCache() {
		String urlString = "http://localhost:9000/wro/wroAPI/reloadCache";
		try {
			httpUtility.makeHttpRequest(urlString, "GET");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getFormattedProperties() {
		StringBuilder formattedProperties = new StringBuilder();
		StringBuilder formattedComments = new StringBuilder();
		formattedComments.append("/* The ThemeManagerService added the following SASS vars:\n\n");
		for (ThemeProperty p : this.getCurrentTheme().getProperties()) {
			formattedProperties.append("$"+p.getPropertyName().getName()+": "+p.getValue()+";\n");
			formattedComments.append("* $"+p.getPropertyName().getName()+": "+p.getValue()+";\n");
		}
		formattedComments.append("*/\n\n");
		return formattedComments+formattedProperties.toString();
	}

	public void setCurrentTheme(CoreTheme theme) {
		if (theme.getId() != this.getCurrentTheme().getId()) {
			coreThemeRepo.updateActiveTheme(theme);
			this.currentTheme = theme;
			this.reloadCache();
		}
	}
}
