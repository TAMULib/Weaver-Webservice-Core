/* 
 * StompConnectEvent.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.events;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import edu.tamu.framework.service.StompConnectionService;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {
	 
	private static final Logger logger = Logger.getLogger(StompConnectEvent.class);
 
	@Autowired
	private StompConnectionService stompConnectionService;
	
	@Autowired
	private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        stompConnectionService.incrementActiveConnections();
        logger.debug("ApplicationListener: Connect event [sessionId: " + sha.getSessionId() + "]");
	    logger.debug("Timestamp: " + event.getTimestamp());
	    logger.debug("Message: " + event.getMessage());	    
        logger.debug("ApplicationListener: Total number of web socket connections: " + stompConnectionService.getActiveConnections());        
        logger.debug(webSocketMessageBrokerStats.getWebSocketSessionStatsInfo());
    }
}