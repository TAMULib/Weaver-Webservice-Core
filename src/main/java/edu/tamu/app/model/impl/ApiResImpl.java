package edu.tamu.app.model.impl;

import edu.tamu.app.model.APIres;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * API response object implimentation.
 * 
 * @author 
 */
public class ApiResImpl extends APIres {
    
	/**
	 * API response implementation constructor with list of objects.
	 * 
	 * @param response
	 * @param objects
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
     * API response implementation constructor without list of objects.
     * 
     * @param response
     */
    public ApiResImpl(String response) {
        this.response = response;
        this.content = null;
    }
	
}
