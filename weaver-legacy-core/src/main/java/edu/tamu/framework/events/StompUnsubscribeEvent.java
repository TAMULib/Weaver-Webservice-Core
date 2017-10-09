/* 
 * StompUnsubscribeEvent.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class StompUnsubscribeEvent implements ApplicationListener<SessionUnsubscribeEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        logger.debug("Unsubscribe event [sessionId: " + event.getMessage().getHeaders().get("sessionId") + "]");
        logger.debug("Timestamp: " + event.getTimestamp());
        logger.debug("Message: " + event.getMessage());
    }

}