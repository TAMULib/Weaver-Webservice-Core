package edu.tamu.framework.mapping;

import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import edu.tamu.framework.aspect.annotation.interfaces.ApiMapping;

public class RestRequestMappingHandler extends RequestMappingHandlerMapping {
	
	@Override
	protected boolean isHandler(Class<?> beanType) {
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(beanType);
		for (Method method : methods) {
			if (AnnotationUtils.findAnnotation(method, ApiMapping.class) != null) {

				System.out.println("\n\nREST IS HANDLER\n\n");
				
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
		
		System.out.println("\n\nHANDLE MATCH\n\n");
		
		request.setAttribute("ApiMapping", true);
		request.setAttribute("handledTime", System.nanoTime());
	}
	
	@Override
	protected HandlerMethod handleNoMatch(Set<RequestMappingInfo> requestMappingInfos, String lookupPath, HttpServletRequest request) throws ServletException {
		
		System.out.println("\n\n" + lookupPath);
		
		System.out.println("\n\n" + request.getServletPath());
		
		System.out.println("\n\nHANDLE NOT MATCHED\n\n");
		
		return null;
	}
	
	@Override
	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		
		RequestMappingInfo info = null;
		
		System.out.println("\n\nGET MAPPING FOR METHOD\n\n");

		Mapping mapping = new Mapping();
		
		ApiMapping methodAnnotation = AnnotationUtils.findAnnotation(method, ApiMapping.class);
		
		if(methodAnnotation != null) {
			
			System.out.println("\n\nFOUND API MAPPING\n\n");
			
			for(String value : methodAnnotation.value()) {
				System.out.println("\nMETHOD: " + value + "\n");
			}
			
			mapping.path = methodAnnotation.value();
			mapping.method = new RequestMethod[] {methodAnnotation.method()};
		
		
			ApiMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiMapping.class);
			
			if(typeAnnotation != null) {
				for(String value : typeAnnotation.value()) {
					System.out.println("\nTYPE: " + value + "\n");
				}
				
				if(mapping.path.length == typeAnnotation.value().length) {				
					for(int i = 0; i < mapping.path.length; i++) {
						mapping.path[i] = typeAnnotation.value()[i] + mapping.path[i];
					}				
				} 
				else if(mapping.path.length < typeAnnotation.value().length) {
					for(int i = 0; i < mapping.path.length; i++) {
						mapping.path[i] = typeAnnotation.value()[i] + mapping.path[i];
					}
				} 
				else {
					for(int i = 0; i < typeAnnotation.value().length; i++) {
						mapping.path[i] = typeAnnotation.value()[i] + mapping.path[i];
					}
				}				
			}
			
			RequestCondition<RestRequestCondition> methodCondition = getCustomMethodCondition(method);
			
			info = createRequestMappingInfo(mapping, methodCondition);
		}
		
		return info;
	}
	
	protected RequestMappingInfo createRequestMappingInfo(Mapping mapping, RequestCondition<RestRequestCondition> customCondition) {
		
		
		System.out.println("\n\nGET REQUEST MAPPING INFO\n\n");
		
		ContentNegotiationManager cnm = getContentNegotiationManager();
		
		if(cnm == null) {
			System.out.println("\n\nPROBLEM CNM NULL\n\n");
		}
		
		return new RequestMappingInfo(
				new PatternsRequestCondition(mapping.path),
				new RequestMethodsRequestCondition(mapping.method),
				new ParamsRequestCondition(new String[]{}),
				new HeadersRequestCondition(new String[] {}),
				new ConsumesRequestCondition(new String[]{}, new String[]{}),
				new ProducesRequestCondition(new String[]{}, new String[]{}, cnm),
				customCondition);
	}
	
	
	@Override
	protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
		ApiMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiMapping.class);
		
		System.out.println("\n\nGETTING CUSTOM TYPE CONDITION\n\n");
		
	    return createCondition(typeAnnotation);
	}
	
	@Override
	protected RequestCondition<RestRequestCondition> getCustomMethodCondition(Method method) {
		ApiMapping methodAnnotation = AnnotationUtils.findAnnotation(method, ApiMapping.class);
		
		System.out.println("\n\nGETTING CUSTOM METHOD CONDITION\n\n");
		
	    return createCondition(methodAnnotation);
	}
	
	private RequestCondition<RestRequestCondition> createCondition(ApiMapping accessMapping) {
		
		for(String value : accessMapping.value()) {
			System.out.println("\n" + value + "\n");
		}
				
	    return (accessMapping != null) ? new RestRequestCondition(accessMapping.value()) : null;
	}
	
	
	public class Mapping {
		public String[] path;
		public RequestMethod[] method;
		public Mapping() {}
		public Mapping(String[] path, RequestMethod[] method) {
			this.path = path;
			this.method = method;
		}
	}
	
}

