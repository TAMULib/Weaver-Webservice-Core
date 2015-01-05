package edu.tamu.app.model;

import java.util.HashMap;

public abstract class APIres{
	
	public String response;
    public HashMap<String, Object> content;

	public String getResponse() {
		return response;
	}
	
	public Object getContent() {
		return content;
	}
   
}
