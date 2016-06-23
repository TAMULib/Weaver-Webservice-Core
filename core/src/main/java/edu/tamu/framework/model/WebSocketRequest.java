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

/**
 * Websocket request. Created and stored in memory when a new request goes
 * through the interceptor. Is retrieved and removed when the aspect point cuts
 * an endpoint.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class WebSocketRequest {

	private Message<?> message;

	private String user;

	private String destination;
	
	private Credentials credentials;

	public WebSocketRequest() {}

	public WebSocketRequest(Message<?> message, String user, String destination) {
		this.message = message;
		this.user = user;
		this.destination = destination;
	}

	/**
	 * Gets message.
	 * 
	 * @return Message<?>
	 */
	public Message<?> getMessage() {
		return message;
	}

	/**
	 * Sets message.
	 * 
	 * @param message
	 *            Message<?>
	 */
	public void setMessage(Message<?> message) {
		this.message = message;
	}

	/**
	 * Gets user.
	 * 
	 * @return String
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets user.
	 * 
	 * @param user
	 *            String
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets destination.
	 * 
	 * @return String
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Sets destination.
	 * 
	 * @param destination
	 *            String
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

    /**
     * @return the credentials
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * @param credentials the credentials to set
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
	
}
