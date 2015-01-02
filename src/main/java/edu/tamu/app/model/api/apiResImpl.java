package edu.tamu.app.model.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class apiResImpl extends APIres {
    
    public apiResImpl(String response, Object ... objects) {
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
    
    public apiResImpl(String response) {
        this.response = response;
        this.content = null;
    }
	
}
