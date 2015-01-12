package edu.tamu.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
 
/**
 * 
 * @author 
 */
@Configuration
@ComponentScan("edu.tamu.app")
public class TestDataSourceConfiguration {

	@Autowired
    private Environment environment;
 
	/**
	 * Embedded data source bean.
	 * 
	 * @return EmbeddedDatabase
	 */
    @Bean
    public EmbeddedDatabase dataSource() {
        
    	 return new EmbeddedDatabaseBuilder().
    	            setType(EmbeddedDatabaseType.H2).
    	            build();
    }
	
}
