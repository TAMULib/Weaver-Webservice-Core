package edu.tamu.framework.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:rmathew@library.tamu.edu">Rincy Mathew</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
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
