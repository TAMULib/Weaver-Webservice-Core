package edu.tamu.app.model.impl;

import java.util.ArrayList;
import java.util.HashMap;

import edu.tamu.app.model.APIres;

public class ApiResImpl extends APIres {
    
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
    
    public ApiResImpl(String response) {
        this.response = response;
        this.content = null;
    }
	
}
