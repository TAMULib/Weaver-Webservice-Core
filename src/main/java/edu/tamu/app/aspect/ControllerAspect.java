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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.aspect.annotation.Auth;
import edu.tamu.app.model.Credentials;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.UserImpl;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.app.util.HttpRequestUtility;
import edu.tamu.app.util.WebSocketRequestUtility;
import edu.tamu.app.enums.Roles;

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
	
	@Autowired
	private WebSocketRequestUtility webSocketRequestUtility;
	
	@Autowired
	private HttpRequestUtility httpRequestUtility;
	
	@Autowired
	private SecurityContext securityContext;

    @Around("execution(* edu.tamu.app.controller.*.*(..)) && !@annotation(edu.tamu.app.aspect.annotation.SkipAop) && @annotation(auth)")
    public ApiResImpl polpulateCredentialsAndAuthorize(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
    	
    	PreProcessObject preProcessObject = preProcess(joinPoint);
    	
    	if(preProcessObject.error != null) {
    		return preProcessObject.error;
    	}
        
        if(Roles.valueOf(preProcessObject.shib.getRole()).ordinal() < Roles.valueOf(auth.role()).ordinal()) {
        	System.out.println("DENIED");
        	return new ApiResImpl("restricted", "You are not authorized for this request.", new RequestId(preProcessObject.requestId));
        }
                
        return (ApiResImpl) joinPoint.proceed(preProcessObject.arguments);	
		
    }
    
    @Around("execution(* edu.tamu.app.controller.*.*(..)) && !@annotation(edu.tamu.app.aspect.annotation.SkipAop) && !@annotation(edu.tamu.app.aspect.annotation.Auth)")
    public ApiResImpl populateCredentials(ProceedingJoinPoint joinPoint) throws Throwable {
    	
    	PreProcessObject preProcessObject = preProcess(joinPoint);
    	
    	if(preProcessObject.error != null) {
    		return preProcessObject.error;
    	}
    	
        return (ApiResImpl) joinPoint.proceed(preProcessObject.arguments);
        
    }
    
    private PreProcessObject preProcess(ProceedingJoinPoint joinPoint) throws Throwable {
    	    	    	
    	HttpServletRequest request = null;
    	
        Message<?> message = null;
    	StompHeaderAccessor accessor = null;
    	
    	Credentials shib = null;
    	String requestId = null;
    	String data = null;
    	
    	
    	Object[] arguments = joinPoint.getArgs();
    	
    	MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> clazz = methodSignature.getDeclaringType();
        Method method = clazz.getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        
    	if (RequestContextHolder.getRequestAttributes() != null) {
    		
    		request = httpRequestUtility.getAndRemoveRequestByDestinationAndUser(method.getAnnotation(RequestMapping.class).value()[0], securityContext.getAuthentication().getName());
    		
    		shib = (Credentials) request.getAttribute("shib");
    		
    		data = (String) request.getAttribute("data");
    		
    	} else {
    		
    		message = webSocketRequestUtility.getAndRemoveMessageByDestinationAndUser(method.getAnnotation(MessageMapping.class).value()[0], securityContext.getAuthentication().getName());
    		
    		accessor = StompHeaderAccessor.wrap(message);
    		
    		requestId = accessor.getNativeHeader("id").get(0);
    		shib =(Credentials) accessor.getSessionAttributes().get("shib");
    		    		
    		data = accessor.getNativeHeader("data").get(0).toString();
    	}  
    	    	
		shib = authorizeRole(shib);
				
		Map<String, Integer> argMap = new HashMap<String, Integer>();
  		
  		int index = 0;
  		for (Annotation[] annotations : method.getParameterAnnotations()) {
  			
  			for (Annotation annotation : annotations) {

  				String annotationString = annotation.toString();
  				annotationString = annotationString.substring(annotationString.lastIndexOf('.')+1).replace("()", "");
		
  				argMap.put(annotationString, index);
            
  			}
  			index++;
  		}
  		
  		for(String arg : argMap.keySet()) {
  	
  			switch(arg) {	  			
	  			case "Shib": {
	  				arguments[argMap.get(arg)] = shib;
	  			} break;
	  			case "ReqId": {
	  				arguments[argMap.get(arg)] = requestId;
	  			} break;
	  			case "Data": {
	  				arguments[argMap.get(arg)] = data;
	  			} break;
	  			case "InputStream": {
	  				arguments[argMap.get(arg)] = request.getInputStream();
	  			} break;
  			}
  	
  		}
		        
    	return new PreProcessObject(shib, requestId, arguments);
    }
    
    private Credentials authorizeRole(Credentials shib) {
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
		return shib;
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
	
}
