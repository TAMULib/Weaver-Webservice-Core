package edu.tamu.app.model;

import javax.servlet.http.HttpServletRequest;

public class HttpRequest {
	
	private HttpServletRequest request;
	
	private String user;	
	
	public HttpRequest(HttpServletRequest request, String user) {
		this.request = request;
		this.user = user;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
