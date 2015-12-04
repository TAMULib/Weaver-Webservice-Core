package edu.tamu.framework.events;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {
	 
	private static final Logger logger = Logger.getLogger(SessionDisconnectEvent.class);
	
	@Autowired
	private StompConnectionService stompConnectionService;
 
	@Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        stompConnectionService.decrementActiveConnections();
        logger.debug("Disconnect event [sessionId: " + sha.getSessionId() + "]");
        logger.debug("Total number of web socket connections: " + stompConnectionService.getActiveConnections());
    }
}