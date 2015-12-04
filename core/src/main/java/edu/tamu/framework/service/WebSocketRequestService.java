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

	public List<WebSocketRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<WebSocketRequest> requests) {
		this.requests = requests;
	}
	
	public synchronized void addRequest(WebSocketRequest request) {
		requests.add(request);
	}
	
	public synchronized void removeRequest(WebSocketRequest request) {
		requests.remove(request);
	}
	
	public synchronized WebSocketRequest getAndRemoveMessageByDestinationAndUser(String destination, String user) {
		for(int index = 0; index < requests.size(); index++) {			
			WebSocketRequest request = requests.get(index);
			if(request.getUser().equals(user) && request.getDestination().contains(destination)) {
				requests.remove(index);
				return request;
			}
		}		
		return null;
	}
	
}
