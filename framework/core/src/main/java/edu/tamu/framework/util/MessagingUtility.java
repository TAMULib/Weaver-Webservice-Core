package edu.tamu.framework.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingUtility {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	public void convertAndSend(String destination, Object payload) throws MessagingException {
		simpMessagingTemplate.convertAndSend(destination, payload);
	}
	
}
