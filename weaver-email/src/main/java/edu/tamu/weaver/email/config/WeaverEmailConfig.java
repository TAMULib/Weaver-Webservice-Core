package edu.tamu.weaver.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.tamu.weaver.email.service.EmailSender;
import edu.tamu.weaver.email.service.WeaverEmailService;

@Configuration
@Profile(value = { "!test" })
public class WeaverEmailConfig {

    @Value("${app.email.host:relay.tamu.edu}")
    private String defaultHost;

    @Value("${app.email.from:noreply@library.tamu.edu}")
    private String defaultFrom;

    @Value("${app.email.replyTo:dev@library.tamu.edu}")
    private String defaultReplyTo;

    @Bean
    public EmailSender emailSender() {
        WeaverEmailService emailService = new WeaverEmailService();
        if (defaultHost != null) {
            emailService.setHost(defaultHost);
        }
        if (defaultFrom != null) {
            emailService.setFrom(defaultFrom);
        }
        if (defaultReplyTo != null) {
            emailService.setReplyTo(defaultReplyTo);
        }
        return emailService;
    }

}
