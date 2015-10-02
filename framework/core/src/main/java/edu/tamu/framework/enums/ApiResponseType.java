package edu.tamu.framework.enums;

public enum ApiResponseType {
	
	SUCCESS("Your request was successful"),
	REFRESH("Your token has expired"), 
	ERROR("Your request caused an error"), 
	WARNING("Your request caused warning"), 
	INFO("Your request was processed");

	String message;
	
	ApiResponseType(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
}
