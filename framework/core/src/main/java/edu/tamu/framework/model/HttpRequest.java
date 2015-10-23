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

public class HttpRequest {
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private String user;
		
	public HttpRequest(HttpServletRequest request, HttpServletResponse response, String user) {
		this.request = request;
		this.response = response;
		this.user = user;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getDestination() {
		return request.getRequestURI();
	}
	
}
