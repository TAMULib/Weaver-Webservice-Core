/* 
 * StompConnectEvent.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import edu.tamu.weaver.service.StompService;

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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StompService stompService;

    @Autowired
    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        stompService.incrementActiveConnections();
        logger.debug("ApplicationListener: Connect event [sessionId: " + sha.getSessionId() + "]");
        logger.debug("Timestamp: " + event.getTimestamp());
        logger.debug("Message: " + event.getMessage());
        logger.debug("ApplicationListener: Total number of web socket connections: " + stompService.getActiveConnections());
        logger.debug(webSocketMessageBrokerStats.getWebSocketSessionStatsInfo());
    }

}