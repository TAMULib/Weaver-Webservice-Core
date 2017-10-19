
package edu.tamu.weaver.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.tamu.weaver.email.service.EmailSender;
import edu.tamu.weaver.email.service.MockEmailService;

@Configuration
@Profile(value = { "test" })
public class MockEmailConfig extends WeaverEmailConfig {

    @Override
    @Bean
    public EmailSender emailSender() {
        return new MockEmailService();
    }

}
