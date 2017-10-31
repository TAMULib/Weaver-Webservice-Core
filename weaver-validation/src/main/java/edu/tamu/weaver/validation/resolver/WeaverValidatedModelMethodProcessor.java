package edu.tamu.weaver.validation.resolver;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

public class WeaverValidatedModelMethodProcessor extends RequestResponseBodyMethodProcessor {

    public WeaverValidatedModelMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WeaverValidatedModel.class);
    }

    protected boolean checkRequired(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(WeaverValidatedModel.class).required() && !parameter.isOptional());
    }

}