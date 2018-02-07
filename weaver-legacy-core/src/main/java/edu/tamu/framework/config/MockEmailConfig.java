/* 
 * MockEmailConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.tamu.weaver.util.EmailSender;
import edu.tamu.weaver.util.MockEmailUtility;

@Configuration
@Profile(value = { "test" })
public class MockEmailConfig extends CoreEmailConfig {

    @Override
    @Bean
    public EmailSender emailSender() {
        return new MockEmailUtility();
    }

}
