/* 
 * APIres.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import java.util.HashMap;

/**
 * Abstract class for an API response.
 * 
 * @author 
 *
 */
public abstract class APIres{
	
	public String response;
    public HashMap<String, Object> content;
    
    /**
     * Gets response.
     * 
     * @return		String
     * 
     */
	public String getResponse() {
		return response;
	}
	
	/**
     * Gets content.
     * 
     * @return		Object
     * 
     */
	public Object getContent() {
		return content;
	}
   
}
