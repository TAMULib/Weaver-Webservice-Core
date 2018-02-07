/* 
 * CoreEmailConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.tamu.weaver.util.CoreEmailUtility;
import edu.tamu.weaver.util.EmailSender;

@Configuration
@Profile(value = { "!test" })
public class CoreEmailConfig {

    @Value("${app.email.host}")
    private String defaultHost;

    @Value("${app.email.from}")
    private String defaultFrom;

    @Value("${app.email.replyTo}")
    private String defaultReplyTo;

    @Bean
    public EmailSender emailSender() {
        CoreEmailUtility emailUtility = new CoreEmailUtility();
        if (defaultHost != null) {
            emailUtility.setHost(defaultHost);
        }
        if (defaultFrom != null) {
            emailUtility.setFrom(defaultFrom);
        }
        if (defaultReplyTo != null) {
            emailUtility.setReplyTo(defaultReplyTo);
        }
        return emailUtility;
    }

}