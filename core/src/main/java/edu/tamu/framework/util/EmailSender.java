package edu.tamu.framework.util;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSender;

/**
 * Email Utility
 */
public interface EmailSender extends JavaMailSender {
	
    public void sendEmail(String to, String subject, String text) throws MessagingException;
    
}