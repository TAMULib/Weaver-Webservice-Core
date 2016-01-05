package edu.tamu.framework.events;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import edu.tamu.framework.service.StompConnectionService;

public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {
	 
	private static final Logger logger = Logger.getLogger(StompDisconnectEvent.class);
	
	@Autowired
	private StompConnectionService stompConnectionService;
	
	@Autowired
	private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
 
	@Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
	    stompConnectionService.decrementActiveConnections();
	    logger.debug("Disconnect event [sessionId: " +  event.getSessionId() + "]");
	    logger.debug("Timestamp: " + event.getTimestamp());
	    logger.debug("Status: " + event.getCloseStatus());
	    logger.debug("Message: " + event.getMessage());
	    logger.debug("ApplicationListener: Total number of web socket connections: " + stompConnectionService.getActiveConnections());
        logger.debug(webSocketMessageBrokerStats.getWebSocketSessionStatsInfo());
    }
	
}