package edu.tamu.framework.model;

import org.springframework.messaging.Message;

public class WebSocketRequest {
	
	private Message<?> message;
	
	private String user;
	
	private String destination;
	
	public WebSocketRequest(Message<?> message, String destination, String user) {
		this.message = message;
		this.destination = destination;
		this.user = user;
	}

	public Message<?> getMessage() {
		return message;
	}

	public void setMessage(Message<?> message) {
		this.message = message;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
