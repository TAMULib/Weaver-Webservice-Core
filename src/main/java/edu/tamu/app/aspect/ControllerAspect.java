/* 
 * ControllerAspect.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.aspect.annotation.Auth;
import edu.tamu.app.aspect.annotation.PreProcess;
import edu.tamu.app.model.Credentials;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.model.repo.UserRepo;

/** 
 * Controller Aspect
 * 
 * @author
 *
 */
@Component
@Aspect
public class ControllerAspect {
	
	@Value("${app.security.jwt.secret_key}") 
	private String secret_key;
	
	@Value("${app.authority.admins}")
	private String[] admins;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	public ObjectMapper objectMapper;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate;
		
    @Around(value="@annotation(auth)")
    public ApiResImpl validatePolpulateCredentialsAndAuthorize(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
    	
    	PreProcessObject preProcessObject = validatePreProcess(joinPoint);
    	
    	if(preProcessObject.error != null) {
    		return preProcessObject.error;
    	}
        
        System.out.println("YOUR ROLE " + Roles.valueOf(preProcessObject.shib.getRole()));
        System.out.println("ATTEMPTING ACCESS AGAINST " + Roles.valueOf(auth.role()));
        
        if(Roles.valueOf(preProcessObject.shib.getRole()).ordinal() < Roles.valueOf(auth.role()).ordinal()) {
        	System.out.println("DENIED");
        	return new ApiResImpl("restricted", "You are not authorized for this request.", new RequestId(preProcessObject.requestId));
        }
        
        System.out.println("GRANTED");
                
        return (ApiResImpl) joinPoint.proceed(preProcessObject.arguments);	
		
    }
    
    @Around(value="@annotation(preProcess)")
    public ApiResImpl validateAndPopulateCredentials(ProceedingJoinPoint joinPoint, PreProcess preProcess) throws Throwable {
    	
    	PreProcessObject preProcessObject = validatePreProcess(joinPoint);
    	
    	if(preProcessObject.error != null) {
    		return preProcessObject.error;
    	}
    	
        return (ApiResImpl) joinPoint.proceed(preProcessObject.arguments);
        
    }
    
    @SuppressWarnings("unchecked")
	private PreProcessObject validatePreProcess(ProceedingJoinPoint joinPoint) throws Throwable {
    	
    	Object[] arguments = joinPoint.getArgs();
    	
    	MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> clazz = methodSignature.getDeclaringType();
        Method method = clazz.getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        
        RequestType requestType = null;
        
        // for rest request
        HttpServletRequest request = null;
        
        // for web socket request
    	Message<?> message = null;
    	StompHeaderAccessor accessor = null;
    	
    	for(Object argument : arguments) {
    		
            // for rest request
    		if("org.springframework.security.web.servletapi.HttpServlet3RequestFactory$Servlet3SecurityContextHolderAwareRequestWrapper".equals(argument.getClass().getTypeName())) {
    			request = (HttpServletRequest) joinPoint.getArgs()[0];
    			requestType = RequestType.REST;
    		}
    		
    		// for web socket request
    		if("org.springframework.messaging.support.GenericMessage".equals(argument.getClass().getTypeName())) {
    			message = (Message<?>) joinPoint.getArgs()[0];
    	    	accessor = StompHeaderAccessor.wrap(message);
    	    	requestType = RequestType.WEB_SOCKET;
    		}   		
    		
    	}
    	
    	
    	// decode and validate
    	
    	MacSigner hmac = new MacSigner(secret_key);
    	
    	Jwt token = null;
    	
    	String requestId = "0";
    	
    	
    	if(requestType == RequestType.REST) {
    		// safety check
	    	if(request != null) {
	    		
	    		String jwt = request.getHeader("jwt");
	    		
	    		if(jwt == null) {
	    			throw new MissingJwtException();
	    		}
	    		
	    		try {
	    			token = JwtHelper.decodeAndVerify(jwt, hmac);
	    		} catch (Exception e) {
	    			throw new InvalidJwtException();
	    		}
	    		
	    	}
    	}
    	else if(requestType == RequestType.WEB_SOCKET) {
    		// safety check
	    	if(message != null && accessor != null) {
	    		
	    		requestId = accessor.getNativeHeader("id").get(0);
	    		
	    		MessageHeaders headers = message.getHeaders();	
	    		Map<String, Object> headerMap = (Map<String, Object>) headers.get("nativeHeaders");		
	    		
	    		String jwt = headerMap.get("jwt").toString();			
	    		jwt = jwt.substring(1, jwt.length()-1);
	    		
	    		if(jwt == null) {
	    			System.out.println("Missing token!");
	    			return new PreProcessObject(new ApiResImpl("failure", "MISSING_JWT", new RequestId(requestId)));
	    		}
	    		
	    		try {
	    			token = JwtHelper.decodeAndVerify(jwt, hmac);
	    		} catch (Exception e) {
	    			System.out.println("Invalid token! Not verified!");
	    			return new PreProcessObject(new ApiResImpl("failure", "INVALID_UIN", new RequestId(requestId)));
	    		}
	    		
	    	}
	    	
    	}
    	else {
    		// something went wrong
    	}
    	

		// map credentials
    	
    	Map<String, String> tokenMap = null;
		try {			
			tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
		} catch (Exception e) {
			System.out.println("Invalid token! Unable to map!");
			return new PreProcessObject(new ApiResImpl("failure", "INVALID_UIN", new RequestId(requestId)));
		}
		
		Credentials shib = new Credentials(tokenMap);

		if(shib.getRole() == null) {
			
			UserImpl user = userRepo.getUserByUin(Long.parseLong(shib.getUin()));
			
			if(user == null) {
				shib.setRole("ROLE_USER");
				
				String shibUin = shib.getUin();
				for(String uin : admins) {
					if(uin.equals(shibUin)) {
						shib.setRole("ROLE_ADMIN");					
					}
				}
			}
			else {
				shib.setRole(user.getRole());
			}
			
		}

		
		// check expiration
		
		long currentTime = Calendar.getInstance().getTime().getTime()+90000;
		long expTime = Long.parseLong(shib.getExp());
		
		if(expTime < currentTime) {
			System.out.println("Token expired!");
			return new PreProcessObject(new ApiResImpl("refresh", "EXPIRED_JWT", new RequestId(requestId)));
		}
		else {
			System.out.println("Token expires in: " + ((expTime - currentTime)) / 1000);
		}
		
		
		if(requestType == RequestType.REST) {
			request.setAttribute("shib", shib);
		}
		else if(requestType == RequestType.WEB_SOCKET) {	
			Map<String, Object> shibMap = new HashMap<String, Object>();			
			shibMap.put("shib", shib);
			accessor.setSessionAttributes(shibMap);
		}
		
		
		// populate arguments, order contingent
		
        int index = 0;
        for (Annotation[] annotations : method.getParameterAnnotations()) {
        	  for (Annotation annotation : annotations) {
        		  
		            String annotationString = annotation.toString();
		            annotationString = annotationString.substring(annotationString.lastIndexOf('.')+1).replace("()", "");
		            
		            switch(annotationString) {
			            case "Shib": {
			            	arguments[index] = shib;
			            } break;
			            case "ReqId": {
			            	arguments[index] = requestId;
			            } break;
		            }
		            
        	  }
        	  index++;
        }
        
        
    	return new PreProcessObject(shib, requestId, arguments);
    }
    
    public class PreProcessObject {

    	Credentials shib;
    	String requestId;
    	Object[] arguments;
    	ApiResImpl error;
    	
    	public PreProcessObject(ApiResImpl error) {
    		this.error = error;
    	}
    	
    	public PreProcessObject(Credentials shib, Object[] arguments) {
    		this.shib = shib;
    		this.arguments = arguments;
    	}
    	
    	public PreProcessObject(Credentials shib, String requestId, Object[] arguments) {
    		this.shib = shib;
    		this.requestId =requestId;
    		this.arguments = arguments;
    	}
    	
    }
    
    public enum RequestType {
    	REST,
    	WEB_SOCKET
    }
    
    public enum Roles {
    	ROLE_ANONYMOUS, 
    	ROLE_USER, 
    	ROLE_MANAGER, 
    	ROLE_ADMIN
    }
    
    /**
	 * Expired JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="expired") 
	public class ExpiredJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Missing JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="missing") 
	public class MissingJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Invalid JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="invalid") 
	public class InvalidJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
}
