/* 
 * HttpRequest.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http request. Created and stored in memory when a new request goes through the interceptor. Is
 * retrieved and removed when the aspect point cuts an endpoint.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class HttpRequest<U extends AbstractCoreUser> {

    private HttpServletRequest request;

    private HttpServletResponse response;

    private String contextUin;

    private U user;

    private String destination;

    private Credentials credentials;

    public HttpRequest(HttpServletRequest request, HttpServletResponse response, String contextUin, String destination, Credentials credentials) {
        this.request = request;
        this.response = response;
        this.contextUin = contextUin;
        this.destination = destination;
        this.credentials = credentials;
    }

    public HttpRequest(HttpServletRequest request, HttpServletResponse response, String contextUin, String destination, Credentials credentials, U user) {
        this(request, response, contextUin, destination, credentials);
        this.user = user;
    }

    /**
     * Gets request.
     * 
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Sets request.
     * 
     * @param request
     *            HttpServletRequest
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Gets response.
     * 
     * @return HttpServletResponse
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Sets response.
     * 
     * @param response
     *            HttpServletResponse
     */
    public void setResponse(HttpServletResponse response) {
        this.response = response;
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
