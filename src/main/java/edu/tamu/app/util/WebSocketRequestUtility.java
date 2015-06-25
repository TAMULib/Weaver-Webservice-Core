package edu.tamu.app.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.WebSocketRequest;

@Service
public class WebSocketRequestUtility {
	
	private List<WebSocketRequest> requests = new ArrayList<WebSocketRequest>();

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
	
	public synchronized Message<?> getAndRemoveMessageByUser(String user) {
		Message<?> message = null;		
		for(int index = 0; index <= requests.size(); index++) {
			WebSocketRequest request = requests.get(index);
			if(request.getUser().equals(user)) {
				message = request.getMessage();
				requests.remove(index);
				break;
			}
		}
		return message;
	}

}
