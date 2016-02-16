/* 
 * HttpRequestService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.PathMatcher;

import edu.tamu.framework.model.HttpRequest;

/**
 * Http request service. Stores, retrieves, and removes current requests. Used
 * to marshel http requests between interceptor and aspect.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class HttpRequestService {

    @Autowired
    @Lazy
    private PathMatcher pathMatcher;
    
	protected List<HttpRequest> requests = new ArrayList<HttpRequest>();

	/**
	 * Get all current requests.
	 * 
	 * @return List<HttpRequest>
	 */
	public List<HttpRequest> getRequests() {
		return requests;
	}

	/**
	 * Add request.
	 * 
	 * @param request
	 *            WebSocketRequest
	 */
	public synchronized void addRequest(HttpRequest request) {
		if (request.getDestination() != null && request.getUser() != null) {
			requests.add(request);
		}
	}

	/**
	 * Remove request.
	 * 
	 * @param request
	 *            WebSocketRequest
	 */
	public synchronized void removeRequest(HttpRequest request) {
		if (request.getDestination() != null && request.getUser() != null) {
			requests.remove(request);
		}
	}

	/**
	 * Get and remove request.
	 * 
	 * @param pattern
	 *            String
	 * @param user
	 *            String
	 * @return WebSocketRequest
	 */
	public synchronized HttpRequest getAndRemoveRequestByDestinationAndUser(String pattern, String user) {
	    if(pattern.charAt(0) != '/') pattern = "/" + pattern;
		for (int index = 0; index < requests.size(); index++) {
			HttpRequest request = requests.get(index);
			if (request.getUser().equals(user) && pathMatcher.match(pattern, request.getDestination())) {
				requests.remove(index);
				return request;
			}
		}
		return null;
	}

}