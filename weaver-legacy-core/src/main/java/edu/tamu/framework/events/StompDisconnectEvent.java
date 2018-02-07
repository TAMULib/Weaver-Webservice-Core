/* 
 * StompDisconnectEvent.java 
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
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StompService stompService;

    @Autowired
    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        stompService.decrementActiveConnections();
        logger.debug("Disconnect event [sessionId: " + event.getSessionId() + "]");
        logger.debug("Timestamp: " + event.getTimestamp());
        logger.debug("Status: " + event.getCloseStatus());
        logger.debug("Message: " + event.getMessage());
        logger.debug("ApplicationListener: Total number of web socket connections: " + stompService.getActiveConnections());
        logger.debug(webSocketMessageBrokerStats.getWebSocketSessionStatsInfo());
    }

}