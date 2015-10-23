/* 
 * WebSocketRequest.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import org.springframework.messaging.Message;

public class WebSocketRequest {
	
	private Message<?> message;
	
	private String user;
	
	private String destination;
	
	public WebSocketRequest() { }
	
	public WebSocketRequest(Message<?> message, String user, String destination) {
		this.message = message;		
		this.user = user;
		this.destination = destination;
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
