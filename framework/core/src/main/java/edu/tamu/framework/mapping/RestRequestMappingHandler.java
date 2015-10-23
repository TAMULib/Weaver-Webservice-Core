package edu.tamu.framework.mapping;

import java.lang.reflect.Method;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringValueResolver;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import edu.tamu.framework.aspect.annotation.interfaces.ApiMapping;

public class RestRequestMappingHandler extends RequestMappingInfoHandlerMapping implements EmbeddedValueResolverAware {

	private final ContentNegotiationManager contentNegotiationManager;
	
	private StringValueResolver embeddedValueResolver;
	
	public RestRequestMappingHandler(ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}
	
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return AnnotationUtils.findAnnotation(beanType, ApiMapping.class) != null;
	}
	
	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		System.out.println("\nREST SET EMBEDDED VALUE RESOLVER\n");
		this.embeddedValueResolver  = resolver;
	}

	@Override
	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		
		System.out.println("\nREST GET MAPPING FOR METHOD\n");
		
		System.out.println("CLASS: " + handlerType.getName() + "\n");
		
		System.out.println("METHOD: " + method.getName() + "\n");
		
		RequestMappingInfo info = null;
		
		ApiMapping methodAnnotation = AnnotationUtils.findAnnotation(method, ApiMapping.class);
		
		ApiMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiMapping.class);
		
		if (methodAnnotation != null) {
			RequestMappingInfo result = createRequestMappingInfo(methodAnnotation);
			if (typeAnnotation != null) {
				result = createRequestMappingInfo(typeAnnotation).combine(result);
			}
			return result;
		}
		
		return info;
	}
	
	protected RequestMappingInfo createRequestMappingInfo(ApiMapping annotation) {
		return new RequestMappingInfo(
				new PatternsRequestCondition(resolveEmbeddedValuesInPatterns(annotation.value())),
				new RequestMethodsRequestCondition(annotation.method()),
				new ParamsRequestCondition(new String[]{}),
				new HeadersRequestCondition(new String[] {}),
				new ConsumesRequestCondition(new String[]{}, new String[]{}),
				new ProducesRequestCondition(new String[]{MediaType.APPLICATION_JSON_VALUE}, new String[]{}, contentNegotiationManager),
				createCondition(annotation));
	}
	
	protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
		if (this.embeddedValueResolver == null) {
			return patterns;
		}
		else {
			String[] resolvedPatterns = new String[patterns.length];
			for (int i = 0; i < patterns.length; i++) {
				resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
			}
			return resolvedPatterns;
		}
	}
		
	private RequestCondition<RestRequestCondition> createCondition(ApiMapping accessMapping) {
	    return (accessMapping != null) ? new RestRequestCondition(accessMapping.value()) : null;
	}
		
}

