/* 
 * APIres.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract class for an API response.
 * 
 * @author 
 *
 */
public class APIres {
	
	public String response;
    public HashMap<String, Object> content;
    
    /**
   	 * Constructor.
   	 * 
   	 * @param 		response		String
   	 * @param 		objects			Object ...
     * @return 
   	 * 
   	 */
     public APIres(String response, Object ... objects) {
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
        * @return 
        * 
        */
       public APIres(String response) {
           this.response = response;
           this.content = null;
       }
  
    
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
