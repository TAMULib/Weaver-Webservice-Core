package edu.tamu.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import edu.tamu.framework.config.CoreSecurityConfig;

@Configuration
@EnableWebSecurity
@Order(101)
public class SecurityConfig extends CoreSecurityConfig {
		
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.disable();
	}
}
