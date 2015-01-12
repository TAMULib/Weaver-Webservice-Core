package edu.tamu.app.model;

import java.util.HashMap;

/**
 * API response object.
 * 
 * @author
 */
public abstract class APIres{
	
	public String response;
    public HashMap<String, Object> content;

    /**
     * 
     * @return String
     */
	public String getResponse() {
		return response;
	}
	
	/**
	 * 
	 * @return Object
	 */
	public Object getContent() {
		return content;
	}
   
}
