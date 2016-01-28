/* 
 * WebSocketRequestService.java 
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

import org.springframework.stereotype.Service;

import edu.tamu.framework.model.WebSocketRequest;

/**
 * Websocket request service. Stores, retrieves, and removes current requests.
 * Used to marshel websocket requests between interceptor and aspect.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class WebSocketRequestService {

	protected List<WebSocketRequest> requests = new ArrayList<WebSocketRequest>();

	/**
	 * Get all current requests.
	 * 
	 * @return List<WebSocketRequest>
	 */
	public List<WebSocketRequest> getRequests() {
		return requests;
	}

	/**
	 * Add request.
	 * 
	 * @param request
	 *            WebSocketRequest
	 */
	public synchronized void addRequest(WebSocketRequest request) {
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
	public synchronized void removeRequest(WebSocketRequest request) {
		if (request.getDestination() != null && request.getUser() != null) {
			requests.remove(request);
		}
	}

	/**
	 * Get and remove request.
	 * 
	 * @param destination
	 *            String
	 * @param user
	 *            String
	 * @return WebSocketRequest
	 */
	public synchronized WebSocketRequest getAndRemoveMessageByDestinationAndUser(String destination, String user) {
		for (int index = 0; index < requests.size(); index++) {
			WebSocketRequest request = requests.get(index);
			if (request.getUser().equals(user) && request.getDestination().contains(destination)) {
				requests.remove(index);
				return request;
			}
		}
		return null;
	}

}
