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
 * Websocket request. Created and stored in memory when a new request goes through the interceptor.
 * Is retrieved and removed when the aspect point cuts an endpoint.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class WebSocketRequest<U extends AbstractCoreUser> {

    private Message<?> message;

    private String contextUin;

    private U user;

    private String destination;

    private Credentials credentials;
    
    public WebSocketRequest(Message<?> message, String contextUin, String destination, Credentials credentials) {
        this.message = message;
        this.contextUin = contextUin;
        this.destination = destination;
        this.credentials = credentials;
    }

    public WebSocketRequest(Message<?> message, String contextUin, String destination, Credentials credentials, U user) {
        this(message, contextUin, destination, credentials);
        this.user = user;
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
     * 
     * @return
     */
    public String getContextUin() {
        return contextUin;
    }

    /**
     * 
     * @param contextUin
     */
    public void setContextUin(String contextUin) {
        this.contextUin = contextUin;
    }

    /**
     * Gets user.
     * 
     * @return String
     */
    public U getUser() {
        return user;
    }

    /**
     * Sets user.
     * 
     * @param user
     *            String
     */
    public void setUser(U user) {
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
     * @param credentials
     *            the credentials to set
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
