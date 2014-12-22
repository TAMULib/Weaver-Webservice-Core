package edu.tamu.app.model.api;

public class RESTres extends APIres {

	public RESTres(String response, String content, Object returnObject) {
        this.response = response;
        this.content = content;
        this.returnObject = returnObject;
    }
    
    public RESTres(String response, Object returnObject) {
        this.response = response;
        this.content = returnObject.getClass().getSimpleName();
        this.returnObject = returnObject;
    }
    
    public RESTres(String response, String content) {
        this.response = response;
        this.content = content;
        this.returnObject = null;
    }
    
    public RESTres(String response) {
        this.response = response;
        this.content = null;
        this.returnObject = null;
    }
	
}
