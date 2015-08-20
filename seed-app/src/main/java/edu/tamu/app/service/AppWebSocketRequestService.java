package edu.tamu.app.util;


import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.WebSocketRequestService;

@Service
public class AppWebSocketRequestUtility extends WebSocketRequestService {

	@Override
	public Message<?> getMessageAndSetRequest(String destination,
			String user, int index) {
		
		Message<?> message = null;
		WebSocketRequest request = requests.get(index);
		//Directory App Specific Logic:			
		if(destination.contains("/{netid}/portrait")) {
			
			if(request.getUser().equals(user) && request.getDestination().contains("portrait")) {
				
				message = request.getMessage();
				requests.remove(index);
			}
		} else if (destination.equals("/{netid}")) {
			if(request.getUser().equals(user)) {
				message = request.getMessage();
				requests.remove(index);
			}
		}
		
		return message;
	}

}
