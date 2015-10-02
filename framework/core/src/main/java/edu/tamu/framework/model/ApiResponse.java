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

import edu.tamu.framework.enums.ApiResponseType;

/**
 * Abstract class for an API response.
 * 
 * @author
 *
 */
public class ApiResponse {

	private Meta meta;
	
	private HashMap<String, Object> payload;

	private ApiResponse() {
		meta = new Meta();
		payload = new HashMap<String, Object>();
	}
	
	public ApiResponse(ApiResponseType type) {
		this();
		this.meta.setType(type);
		this.meta.setMessage(type.getMessage());
	}
	
	public ApiResponse(ApiResponseType type, String message, Object... payload) {
		this(type, payload);
		this.meta.setMessage(message);
	}
	
	public ApiResponse(String id, ApiResponseType type, String message, Object... payload) {
		this(type, payload);
		this.meta.setId(id);
		this.meta.setMessage(message);
	}
	
	public ApiResponse(String id, ApiResponseType type, Object... payload) {
		this(type, payload);
		this.meta.setId(id);
	}
	
	public ApiResponse(ApiResponseType type, Object... payload) {

		this();
		this.meta.setType(type);
		this.meta.setMessage(this.meta.message != null ? this.meta.message : type.getMessage());   
		
		for (Object obj : payload) {

			String objectType = obj.getClass().getSimpleName();

			if (objectType.equals("ArrayList")) {
				ArrayList<?> a = ((ArrayList<?>) obj);
				if (a.size() > 0)
					objectType += "<" + a.get(0).getClass().getSimpleName() + ">";
			}

			this.payload.put(objectType, obj);
		}

	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public HashMap<String, Object> getPayload() {
		return payload;
	}

	public void setPayload(HashMap<String, Object> payload) {
		this.payload = payload;
	}
	
	public class Meta {
		
		private ApiResponseType type;
		private String message;
		private String id;
		
		public Meta() {}
		
		public ApiResponseType getType() {
			return type;
		}
		public void setType(ApiResponseType type) {
			this.type = type;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
	
}
