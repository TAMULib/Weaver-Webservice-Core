package edu.tamu.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
 
@Configuration
@ComponentScan(basePackages={"edu.tamu.framework", "edu.tamu.app"})
@PropertySource("classpath:/config/application.properties")
public class TestDataSourceConfiguration {

	@Autowired
    private Environment environment;
 
    @Bean
    public EmbeddedDatabase dataSource() {
        
    	 return new EmbeddedDatabaseBuilder().
    	            setType(EmbeddedDatabaseType.H2).
    	            build();
    }
	
}
