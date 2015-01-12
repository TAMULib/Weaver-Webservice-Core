package edu.tamu.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring application initializer.
 * 
 * @author 
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ApplicationInitializer {
	
	/**
	 * Entry point to app if running with mvn spring-boot:run
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApplicationInitializer.class, args);
	}
	
}
