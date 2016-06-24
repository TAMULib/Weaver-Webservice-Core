/* 
 * StompConnectionService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.service;

import org.springframework.stereotype.Service;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class StompConnectionService {

    private static int totalActiveConnections = 0;

    public synchronized void incrementActiveConnections() {
        totalActiveConnections++;
    }

    public synchronized void decrementActiveConnections() {
        totalActiveConnections--;
    }

    public synchronized int getActiveConnections() {
        return totalActiveConnections;
    }

}
