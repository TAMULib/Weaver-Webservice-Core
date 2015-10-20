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

import javax.xml.transform.Source;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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

import edu.tamu.framework.events.StompConnectEvent;
import edu.tamu.framework.events.StompDisconnectEvent;
import edu.tamu.framework.service.StompConnectionService;

/** 
 * Web MVC Configuration for application controller.
 * 
 * @author
 *
 */
@Configuration
@ComponentScan(basePackages = {"edu.tamu.framework.config", "edu.tamu.framework.interceptor", "edu.tamu.framework.controller"})
@ConfigurationProperties(prefix="app.controller")
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
		
}
