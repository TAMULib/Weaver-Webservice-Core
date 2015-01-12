package edu.tamu.app;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Spring webapp initializer.
 * 
 * @author 
 */
public class WebInitializer extends SpringBootServletInitializer {

	/**
	 * Entry point to webapp if deploying in servlet container.
	 * 
	 * @param application
	 */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationInitializer.class);
    }

}
