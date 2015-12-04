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
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
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
