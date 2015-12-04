package edu.tamu.framework.service;

import org.springframework.stereotype.Service;

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
