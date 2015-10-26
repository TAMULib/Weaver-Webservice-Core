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

import org.springframework.stereotype.Service;

import edu.tamu.framework.model.HttpRequest;

@Service
public class HttpRequestService {
	
	protected List<HttpRequest> requests = new ArrayList<HttpRequest>();

	public List<HttpRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<HttpRequest> requests) {
		this.requests = requests;
	}
	
	public synchronized void addRequest(HttpRequest request) {
		requests.add(request);
	}
	
	public synchronized void removeRequest(HttpRequest request) {
		requests.remove(request);
	}
	
	public synchronized HttpRequest getAndRemoveRequestByDestinationAndUser(String destination, String user) {		
		for(int index = 0; index < requests.size(); index++) {		
			HttpRequest request = requests.get(index);			
			if(request.getUser().equals(user) && request.getDestination().contains(destination)) {
				requests.remove(index);
				return request;
			}
		}		
		return null;
	}
	
}