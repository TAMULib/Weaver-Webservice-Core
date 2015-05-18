/* 
 * ApplicationContextProvider.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Sets an application context for use during initialization of the app.
 * Used to start watcher service during app startup.
 * 
 * @author 
 *
 */
@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ApplicationContextProvider implements ApplicationContextAware {
	
	public static ApplicationContext appContext;
	
	public ApplicationContextProvider() {}

	/**
	 * Sets the application context.
	 * 
	 * @param		ac				ApplicationContext
	 * 
	 * @exception   BeansException
	 * 
	 */
	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		
		appContext = ac;
		
	}
	
}