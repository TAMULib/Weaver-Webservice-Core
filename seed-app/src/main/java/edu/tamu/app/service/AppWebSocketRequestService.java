/* 
 * AppWebSocketRequestService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;


import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.WebSocketRequestService;

/**
 * Class AppWebSocketRequestService
 * 
 * @author
 */
@Service
public class AppWebSocketRequestService extends WebSocketRequestService {

	/**
	 * gets message and sets request 
	 * 
	 * @param       destination     String
	 * @param       user            String
	 * @param       index           int
	 * 
	 * @see edu.tamu.framework.service.WebSocketRequestService#getMessageAndSetRequest(java.lang.String, java.lang.String, int)
	 */
	@Override
	public Message<?> getMessageAndSetRequest(String destination, String user, int index) {
		
		WebSocketRequest request = requests.get(index);
		
		return request.getMessage();
	}

}
