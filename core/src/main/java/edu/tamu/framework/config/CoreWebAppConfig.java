/* 
 * WebAppConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.config;

import java.util.List;
import java.util.Properties;

import javax.xml.transform.Source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.handler.factory.SimpleRequestHandlerFactory;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import wro4j.http.handler.CustomRequestHandler;
import edu.tamu.framework.service.ThemeManagerService;
import edu.tamu.framework.wro4j.manager.factory.CustomConfigurableWroManagerFactory;
import edu.tamu.framework.events.StompConnectEvent;
import edu.tamu.framework.events.StompDisconnectEvent;
import edu.tamu.framework.service.StompConnectionService;

/** 
 * Web MVC Configuration for application controller.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Configuration
@ComponentScan(basePackages = {"edu.tamu.framework.config", "edu.tamu.framework.interceptor", "edu.tamu.framework.controller"})
public class CoreWebAppConfig extends WebMvcConfigurerAdapter {	
	
	/**
	 * Configures message converters.
	 *
	 * @param       converters    	List<HttpMessageConverter<?>>
	 *
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
	    stringConverter.setWriteAcceptCharset(false);
	    converters.add(new ByteArrayHttpMessageConverter());
	    converters.add(stringConverter);
	    converters.add(new ResourceHttpMessageConverter());
	    converters.add(new SourceHttpMessageConverter<Source>());
	    converters.add(new AllEncompassingFormHttpMessageConverter());
	    converters.add(jackson2Converter());
	}

	/**
	 * Set object mapper to jackson converter bean.
	 *
	 * @return      MappingJackson2HttpMessageConverter
	 *
	 */
	@Bean
	public MappingJackson2HttpMessageConverter jackson2Converter() {
	    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    converter.setObjectMapper(objectMapper());
	    return converter;
	}
	
	/**
	 * Object mapper bean.
	 *
	 * @return     	ObjectMapper
	 *
	 */
	@Bean
	public ObjectMapper objectMapper() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);	
	    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	    objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
	    return objectMapper;
	}
	
	/**
	 * Security context bean.
	 * 
	 * @return		SecurityContext
	 * 
	 */
	@Bean
	public SecurityContext securityContext() {
		return SecurityContextHolder.getContext();
	}
	
	@Bean
	public StompConnectionService stopmConnectionService() {
		return new StompConnectionService();
	}
	
	@Bean
	public StompConnectEvent stompConnectEvent() {
		return new StompConnectEvent();
	}
	
	@Bean
	public StompDisconnectEvent stompDisconnectEvent() {
		return new StompDisconnectEvent();
	}
	
	
	/**
	 * WRO Configuration
	 */

	@Autowired
	ThemeManagerService themeManagerService;
	
	@Value("${theme.default.css}")
	String[] defaultResources;
	
    @Bean
    FilterRegistrationBean webResourceOptimizer(Environment env) {
    	FilterRegistrationBean fr = new FilterRegistrationBean();
    	ConfigurableWroFilter filter = new ConfigurableWroFilter();
		Properties props = buildWroProperties(env);
		filter.setProperties(props);
		filter.setWroManagerFactory(new CustomConfigurableWroManagerFactory(props,themeManagerService,defaultResources));
		filter.setRequestHandlerFactory(new SimpleRequestHandlerFactory().addHandler(new CustomRequestHandler()));
    	filter.setProperties(props);
    	fr.setFilter(filter);
    	fr.addUrlPatterns("/wro/*");
    	return fr;
    }
    
    private static final String[] OTHER_WRO_PROP = new String[] { ConfigurableProcessorsFactory.PARAM_PRE_PROCESSORS,
    		ConfigurableProcessorsFactory.PARAM_POST_PROCESSORS };

    private Properties buildWroProperties(Environment env) {
    	Properties prop = new Properties();
    	for (ConfigConstants c : ConfigConstants.values()) {
    		addProperty(env, prop, c.name());
    	}
    	for (String name : OTHER_WRO_PROP) {
    		addProperty(env, prop, name);
    	}
    	return prop;
    }

    private void addProperty(Environment env, Properties to, String name) {
    	String value = env.getProperty("wro." + name);
    	if (value != null) {
    		to.put(name, value);
    	}
    }
		
}
