/* 
 * ApiResImpljava 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.impl;

import java.util.ArrayList;
import java.util.HashMap;

import edu.tamu.app.model.APIres;

/**
 * Implementation for ApiRes object.
 * 
 * @author
 *
 */
public class ApiResImpl extends APIres {

    /**
	 * Constructor.
	 * 
	 * @param 		response		String
	 * @param 		objects			Object ...
	 * 
	 */
    public ApiResImpl(String response, Object ... objects) {
        this.response = response;
        
        HashMap<String, Object> content = new HashMap<String, Object>();
      
        for(Object obj : objects) {
        	
        	String objectType = obj.getClass().getSimpleName();
        	        	
        	if(objectType.equals("ArrayList")) {
        		ArrayList<?> a = ((ArrayList<?>) obj);
        		if(a.size()>0)
        			objectType += "<"+a.get(0).getClass().getSimpleName()+">";
        	}
        	
        	content.put(objectType, obj);
        }
        
        this.content = content;
    }
    
    /**
     * Constructor.
     * 
     * @param 		response		String
     * 
     */
    public ApiResImpl(String response) {
        this.response = response;
        this.content = null;
    }
	
}
