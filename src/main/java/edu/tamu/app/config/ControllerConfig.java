package edu.tamu.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "edu.tamu.app.controller")
@ConfigurationProperties(prefix="app.controller")
public class ControllerConfig {	

	
}
