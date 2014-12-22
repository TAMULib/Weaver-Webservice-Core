package edu.tamu.app.model.api;

public abstract class APIres{
	
	
	public String response;
    public String content;
    public Object returnObject;

	public String getResponse() {
		return response;
	}
	
	public String getContent() {
		return content;
	}
	
	public Object getReturnObject() {
		return returnObject;
	}
   
}
