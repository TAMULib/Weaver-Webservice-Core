/* 
 * CoreEmailUtility.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.util;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Core Email Utility
 */
@Service
public class CoreEmailUtility extends JavaMailSenderImpl implements EmailSender {

    private String from;

    private String replyTo;

    private String channel;

    public CoreEmailUtility() {
        setFrom("default@mailinator.com");
        setReplyTo("default@mailinator.com");
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        Properties properties = super.getJavaMailProperties();

        if (channel.equals("starttls")) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtps.ssl.checkserveridentity", "false");
        }

        if (channel.equals("ssl")) {
            properties.setProperty("mail.smtps.ssl.checkserveridentity", "true");
            properties.setProperty("mail.smtp.starttls.enable", "false");
        }

        super.setJavaMailProperties(properties);

        this.channel = channel;
    }

    // super setters and getters

    public String getHost() {
        return super.getHost();
    }

    public void setHost(String host) {
        super.setHost(host);
    }

    public int getPort() {
        return super.getPort();
    }

    public void setPort(int port) {
        super.setPort(port);
    }

    public String getProtocol() {
        return super.getProtocol();
    }

    public void setProtocol(String protocol) {
        super.setProtocol(protocol);
    }

    public String getDefaultEncoding() {
        return super.getDefaultEncoding();
    }

    public void setDefaultEncoding(String encoding) {
        super.setDefaultEncoding(encoding);
    }

    public String getUsername() {
        return super.getUsername();
    }

    public void setUsername(String username) {
        super.setUsername(username);

        Properties properties = super.getJavaMailProperties();

        if (username != null && getPassword() != null) {
            properties.setProperty("mail.smtp.auth", "true");
        } else {
            properties.setProperty("mail.smtp.auth", "false");
        }

        super.setJavaMailProperties(properties);
    }

    public String getPassword() {
        return super.getPassword();
    }

    public void setPassword(String password) {
        super.setPassword(password);

        Properties properties = super.getJavaMailProperties();

        if (password != null && getUsername() != null) {
            properties.setProperty("mail.smtp.auth", "true");
        } else {
            properties.setProperty("mail.smtp.auth", "false");
        }

        super.setJavaMailProperties(properties);
    }

    @Override
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(from);
        helper.setReplyTo(replyTo);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        send(message);
    }

}