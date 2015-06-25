package edu.tamu.app.model;

import org.springframework.messaging.Message;

public class WebSocketRequest {
	
	private Message<?> message;
	
	private String user;	
	
	public WebSocketRequest(Message<?> message, String user) {
		this.message = message;
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
	
}
