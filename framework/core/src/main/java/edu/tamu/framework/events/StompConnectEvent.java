package edu.tamu.framework.events;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import edu.tamu.framework.service.StompConnectionService;

public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {
	 
	private static final Logger logger = Logger.getLogger(StompConnectEvent.class);
 
	@Autowired
	private StompConnectionService stompConnectionService;
	
	@Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        stompConnectionService.incrementActiveConnection();
        logger.debug("Connect event [sessionId: " + sha.getSessionId() + "]");
        logger.debug("Total number of web socket connections: " + stompConnectionService.getActiveConnections());
    }
}