/* 
 * SecurityConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/** 
 * Web security configuration.
 * 
 * @author
 *
 */
@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
public class CoreSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * Configures web security. Disables cross-site request forgery.
	 *
	 * @param       http    		HttpSecurity
	 *
	 * @exception   Exception
	 * 
	 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable()
    		.headers().frameOptions().disable();
    }
    
}
