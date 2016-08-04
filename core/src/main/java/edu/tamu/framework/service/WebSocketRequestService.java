/* 
 * WebSocketRequestService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.PathMatcher;

import edu.tamu.framework.model.WebSocketRequest;

/**
 * Websocket request service. Stores, retrieves, and removes current requests.
 * Used to marshel websocket requests between interceptor and aspect.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class WebSocketRequestService {

    @Autowired
    @Lazy
    private PathMatcher pathMatcher;

    protected List<WebSocketRequest> requests = new ArrayList<WebSocketRequest>();

    /**
     * Get all current requests.
     * 
     * @return List<WebSocketRequest>
     */
    public List<WebSocketRequest> getRequests() {
        return requests;
    }

    /**
     * Add request.
     * 
     * @param request
     *            WebSocketRequest
     */
    public synchronized void addRequest(WebSocketRequest request) {
        if (request.getDestination() != null && request.getUser() != null) {
            requests.add(request);
        }
    }

    /**
     * Remove request.
     * 
     * @param request
     *            WebSocketRequest
     */
    public synchronized void removeRequest(WebSocketRequest request) {
        if (request.getDestination() != null && request.getUser() != null) {
            requests.remove(request);
        }
    }

    /**
     * Get and remove request.
     * 
     * @param pattern
     *            String
     * @param user
     *            String
     * @return WebSocketRequest
     */
    public synchronized WebSocketRequest getAndRemoveMessageByDestinationAndUser(String pattern, String user) {
        if (pattern.charAt(0) != '/') {
            pattern = "/" + pattern;
        }
        for (int index = 0; index < requests.size(); index++) {
            WebSocketRequest request = requests.get(index);
            if (request.getUser().equals(user) && pathMatcher.match(pattern, request.getDestination())) {
                requests.remove(index);
                return request;
            }
        }
        return null;
    }

}
