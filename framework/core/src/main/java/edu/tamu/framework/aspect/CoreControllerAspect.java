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

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.WARNING;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.interfaces.ApiMapping;
import edu.tamu.framework.enums.CoreRoles;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
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
	
	@Autowired
	public ObjectMapper objectMapper;
	
	@Autowired
	private WebSocketRequestService webSocketRequestService;
	
	@Autowired
	private HttpRequestService httpRequestService;
	
	@Autowired
	private SecurityContext securityContext;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private static final Logger logger = Logger.getLogger(CoreControllerAspect.class);
	
    @Around("execution(* edu.tamu.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && @annotation(auth)")
    public ApiResponse polpulateCredentialsAndAuthorize(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {
    	
    	PreProcessObject preProcessObject = preProcess(joinPoint);
    	        
        if(CoreRoles.valueOf(preProcessObject.shib.getRole()).ordinal() < CoreRoles.valueOf(auth.role()).ordinal()) {
        	logger.info(preProcessObject.shib.getFirstName() + " " + preProcessObject.shib.getLastName() + "(" + preProcessObject.shib.getUin() + ") attempted restricted access.");
            return new ApiResponse(preProcessObject.requestId, ERROR, "You are not authorized for this request.");
        }
                
        ApiResponse apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);
        
        if(apiresponse != null) {
    		apiresponse.getMeta().setId(preProcessObject.requestId);
    	}
    	else {
    		apiresponse = new ApiResponse(WARNING, "Endpoint returns void!");
    	}
        
        if(preProcessObject.protocol == Protocol.WEBSOCKET) {
        	simpMessagingTemplate.convertAndSend(preProcessObject.destination, apiresponse);
        }
    	
        return apiresponse;
    }
    
    @Around("execution(* edu.tamu.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && !@annotation(edu.tamu.framework.aspect.annotation.Auth)")
    public ApiResponse populateCredentials(ProceedingJoinPoint joinPoint) throws Throwable {
    	
    	PreProcessObject preProcessObject = preProcess(joinPoint);
    	    	
    	ApiResponse apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);
    	
    	if(apiresponse != null) {
    		apiresponse.getMeta().setId(preProcessObject.requestId);
    	}
    	else {
    		apiresponse = new ApiResponse(WARNING, "Endpoint returns void!");
    	}
    	
    	if(preProcessObject.protocol == Protocol.WEBSOCKET) {
        	simpMessagingTemplate.convertAndSend(preProcessObject.destination, apiresponse);
        }
    	
        return apiresponse;        
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
		
        Protocol protocol;
        
        String destination = "";
        
    	if (RequestContextHolder.getRequestAttributes() != null) {
    		
    		protocol = Protocol.HTTP;
    		
    		
    		String classAnnotation;
    		
    		String methodAnnotation;
    		
    		if(clazz.getAnnotationsByType(RequestMapping.class).length > 0) {
    			classAnnotation = clazz.getAnnotationsByType(RequestMapping.class)[0].value()[0];
    		}
    		else {
    			classAnnotation = clazz.getAnnotationsByType(ApiMapping.class)[0].value()[0];
    		}
    		
    		if(method.getAnnotation(RequestMapping.class) != null) {
    			methodAnnotation = method.getAnnotation(RequestMapping.class).value()[0];
    		}
    		else {
    			methodAnnotation = method.getAnnotation(ApiMapping.class).value()[0];
    		}
    		    		
    		request = httpRequestService.getAndRemoveRequestByDestinationAndUser(classAnnotation + methodAnnotation, securityContext.getAuthentication().getName());
    		
    		logger.debug("The request: " + request);
    		
    		if(request.getAttribute("shib") != null) {
    			shib = (Credentials) request.getAttribute("shib");
    		}
    		
    		if(request.getAttribute("data") != null) {
    			data = (String) request.getAttribute("data");
    		}
    		
    	} else {
    		
    		protocol = Protocol.WEBSOCKET;
    		
    		message = webSocketRequestService.getAndRemoveMessageByDestinationAndUser(method.getAnnotation(ApiMapping.class).value()[0], securityContext.getAuthentication().getName());
    		
    		logger.debug("The message: " + message);
    		
    		accessor = StompHeaderAccessor.wrap(message);
    		
    		destination = accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId();
    		
    		requestId = accessor.getNativeHeader("id").get(0);
    		
    		shib = (Credentials) accessor.getSessionAttributes().get("shib");

    		if(accessor.getNativeHeader("data") != null) {
    			data = accessor.getNativeHeader("data").get(0).toString();
    		}
    	}  
    	
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
	  			case "Data": {
	  				arguments[argMap.get(arg)] = data;
	  			} break;
	  			case "InputStream": {
	  				arguments[argMap.get(arg)] = request.getInputStream();
	  			} break;
  			}
  	
  		}
  		
		return new PreProcessObject(shib, requestId, arguments, protocol, destination);
    }
    
    protected class PreProcessObject {

    	Credentials shib;
    	String requestId;
    	Object[] arguments;
    	Protocol protocol;
    	String destination;
    	    	
    	public PreProcessObject(Credentials shib, Object[] arguments) {
    		this.shib = shib;
    		this.arguments = arguments;
    	}
    	
    	public PreProcessObject(Credentials shib, String requestId, Object[] arguments) {
    		this.shib = shib;
    		this.requestId = requestId;
    		this.arguments = arguments;
    	}
    	
    	public PreProcessObject(Credentials shib, String requestId, Object[] arguments, Protocol protocol) {
    		this.shib = shib;
    		this.requestId = requestId;
    		this.arguments = arguments;
    		this.protocol = protocol;
    	}
    	
    	public PreProcessObject(Credentials shib, String requestId, Object[] arguments, Protocol protocol, String destination) {
    		this.shib = shib;
    		this.requestId = requestId;
    		this.arguments = arguments;
    		this.protocol = protocol;
    		this.destination = destination;
    	}
    	
    }
    
    private enum Protocol {
    	WEBSOCKET, HTTP
    }
	
}
