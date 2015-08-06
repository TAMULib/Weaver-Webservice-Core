package edu.tamu.framework.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import edu.tamu.framework.model.HttpRequest;

@Service
public abstract class HttpRequestService {
	
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
	
	public synchronized HttpServletRequest getAndRemoveRequestByDestinationAndUser(String destination, String user) {
		
		HttpServletRequest httpServletRequest = null;
		
		for(int index = 0; index < requests.size(); index++) {
		
			HttpRequest request = requests.get(index);
			
			if(request.getUser().equals(user) && request.getDestination().contains(destination)) {
				httpServletRequest = request.getRequest();
				requests.remove(index);
				break;
			}
			
			if(httpServletRequest == null)
				httpServletRequest = getRequestAndSetRequest(destination, user, index);
			
		}
		
		return httpServletRequest;
	}
	
	public abstract HttpServletRequest getRequestAndSetRequest(String destination, String user, int index);

}
