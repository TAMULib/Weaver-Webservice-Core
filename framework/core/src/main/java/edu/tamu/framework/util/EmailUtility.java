package edu.tamu.framework.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class EmailUtility {
	
	@Value("${app.email.host}")
	private String emailHost;
	
	@Value("${app.email.address}")
	private String emailAddress;

	private JavaMailSenderImpl sender;
	
	public EmailUtility() {
		 sender = new JavaMailSenderImpl();
	}
	
	public void sendEmail(String address, String subject, String content) throws MessagingException {
		
		sender.setHost(emailHost);

		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setTo(address);
		helper.setSubject(subject);
		helper.setText(content);

		sender.send(message);
	}
	
	public void sendEmail(String subject, String content) throws MessagingException {
		
		sender.setHost(emailHost);

		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setTo(emailAddress);
		helper.setSubject(subject);
		helper.setText(content);

		sender.send(message);
	}
	
}
