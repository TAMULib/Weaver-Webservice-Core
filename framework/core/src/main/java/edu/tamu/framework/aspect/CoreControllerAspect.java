/* 
 * ControllerAspect.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.aspect;

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

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.enums.CoreRoles;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.RequestId;
import edu.tamu.framework.service.HttpRequestService;
import edu.tamu.framework.service.WebSocketRequestService;

/** 
 * Controller Aspect
 * 
 * @author
 *
 */
@Component
@Aspect
public abstract class CoreControllerAspect {
	
	@Value("${app.security.jwt.secret_key}") 
	private String secret_key;
	
	@Value("${app.authority.admins}")
	String[] admins;
	
	@Autowired
	public ObjectMapper objectMapper;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private WebSocketRequestService webSocketRequestService;
	
	@Autowired
	private HttpRequestService httpRequestService;
	
	@Autowired
	private SecurityContext securityContext;

    @Around("execution(* edu.tamu.app.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && @annotation(auth)")
    public ApiResponse polpulateCredentialsAndAuthorize(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
    	
    	PreProcessObject preProcessObject = preProcess(joinPoint);
    	
    	if(preProcessObject.error != null) {
    		return preProcessObject.error;
    	}
        
        if(CoreRoles.valueOf(preProcessObject.shib.getRole()).ordinal() < CoreRoles.valueOf(auth.role()).ordinal()) {
        	System.out.println("DENIED");
        	return new ApiResponse("restricted", "You are not authorized for this request.", new RequestId(preProcessObject.requestId));
        }
                
        return (ApiResponse) joinPoint.proceed(preProcessObject.arguments);	
		
    }
    
    @Around("execution(* edu.tamu.app.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && !@annotation(edu.tamu.framework.aspect.annotation.Auth)")
    public ApiResponse populateCredentials(ProceedingJoinPoint joinPoint) throws Throwable {
    	
    	PreProcessObject preProcessObject = preProcess(joinPoint);
    	
    	if(preProcessObject.error != null) {
    		return preProcessObject.error;
    	}
    	
        return (ApiResponse) joinPoint.proceed(preProcessObject.arguments);
        
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
    		
    		request = httpRequestService.getAndRemoveRequestByDestinationAndUser(method.getAnnotation(RequestMapping.class).value()[0], securityContext.getAuthentication().getName());
    		
    		shib = (Credentials) request.getAttribute("shib");
    		
    		data = (String) request.getAttribute("data");
    		
    	} else {
    		
    		message = webSocketRequestService.getAndRemoveMessageByDestinationAndUser(method.getAnnotation(MessageMapping.class).value()[0], securityContext.getAuthentication().getName());
    		
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
    
    public abstract String getUserRole(String uin);
    
    private Credentials authorizeRole(Credentials shib) {
    	if(shib.getRole() == null) {
			
			String role = getUserRole(shib.getUin());
			
			if(role == null) {
				shib.setRole("ROLE_USER");
				
				String shibUin = shib.getUin();
				for(String uin : admins) {
					if(uin.equals(shibUin)) {
						shib.setRole("ROLE_ADMIN");					
					}
				}
			}
			else {
				shib.setRole(role);
			}
			
		}
		return shib;
    	
    };
    
    public class PreProcessObject {

    	Credentials shib;
    	String requestId;
    	Object[] arguments;
    	ApiResponse error;
    	
    	public PreProcessObject(ApiResponse error) {
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
