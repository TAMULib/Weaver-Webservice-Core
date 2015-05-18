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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.Credentials;
import edu.tamu.app.model.impl.ApiResImpl;

/** 
 * Controller Aspect
 * 
 * @author
 *
 */
@Component
@Aspect
public class ControllerAspect {
	
    /**
     * Preprocess for MyRecordController and UserController endpoints.
     * Extracts request id from header accessor and shib object from session attributes.
     * 
     * @param 		joinPoint		ProceedingJoinPoint
     * 
     * @return		ApiResImpl
     * 
     * @throws 		Throwable
     * 
     */
    @Around("execution(* edu.tamu.app.controller.*.*(..)) && !@annotation(edu.tamu.app.aspect.annotation.SkipAOP)")
    public ApiResImpl populateCredentials(ProceedingJoinPoint joinPoint) throws Throwable {   	
    	
    	MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> clazz = methodSignature.getDeclaringType();
        Method method = clazz.getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        
    	Object[] arguments = joinPoint.getArgs();
    	    	
    	Message<?> message = null;
    	StompHeaderAccessor accessor = null;
    	for(Object argument : arguments) {
    		if("org.springframework.messaging.support.GenericMessage".equals(argument.getClass().getTypeName())) {
    			message = (Message<?>) joinPoint.getArgs()[0];
    	    	accessor = StompHeaderAccessor.wrap(message);
    		}
    	}
    	
        int index = 0;
        for (Annotation[] annotations : method.getParameterAnnotations()) {
        	  for (Annotation annotation : annotations) {
        		  
		            String annotationString = annotation.toString();
		            annotationString = annotationString.substring(annotationString.lastIndexOf('.')+1).replace("()", "");
		            
		            switch(annotationString) {
			            case "Shib": {
			            	arguments[index] = (Credentials) accessor.getSessionAttributes().get("shib");
			            } break;
			            case "ReqId": {
			            	arguments[index] = accessor.getNativeHeader("id").get(0);
			            } break;
		            }
		            
        	  }
        	  index++;
        }
        
		return (ApiResImpl) joinPoint.proceed(arguments);	
		
    }
        
}
