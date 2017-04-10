/* 
 * HttpRequest<U>Service.java 
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

import edu.tamu.framework.model.AbstractCoreUser;
import edu.tamu.framework.model.HttpRequest;

/**
 * Http request service. Stores, retrieves, and removes current requests. Used
 * to marshel http requests between interceptor and aspect.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class HttpRequestService<U extends AbstractCoreUser> {

    @Autowired
    @Lazy
    private PathMatcher pathMatcher;

    protected List<HttpRequest<U>> requests = new ArrayList<HttpRequest<U>>();

    /**
     * Get all current requests.
     * 
     * @return List<HttpRequest<U>>
     */
    public List<HttpRequest<U>> getRequests() {
        return requests;
    }

    /**
     * Add request.
     * 
     * @param request
     *            HttpRequest<U>
     */
    public synchronized void addRequest(HttpRequest<U> request) {
        if (request.getDestination() != null && request.getUser() != null) {
            requests.add(request);
        }
    }

    /**
     * Remove request.
     * 
     * @param request
     *            HttpRequest<U>
     */
    public synchronized void removeRequest(HttpRequest<U> request) {
        if (request.getDestination() != null && request.getUser() != null) {
            requests.remove(request);
        }
    }

    /**
     * Get and remove request.
     * 
     * @param pattern
     *            String
     * @param uin
     *            Long
     * @return HttpRequest<U>
     */
    public synchronized HttpRequest<U> getAndRemoveRequestByDestinationAndUin(String pattern, String uin) {
        if (pattern.charAt(0) != '/') {
            pattern = "/" + pattern;
        }
        for (int index = 0; index < requests.size(); index++) {
            HttpRequest<U> request = requests.get(index);
            if (request.getUser().getUin().equals(uin) && pathMatcher.match(pattern, request.getDestination())) {
                requests.remove(index);
                return request;
            }
        }
        return null;
    }

}