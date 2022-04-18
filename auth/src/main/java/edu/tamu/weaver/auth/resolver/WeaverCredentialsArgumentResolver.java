package edu.tamu.weaver.auth.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import edu.tamu.weaver.auth.annotation.WeaverCredentials;
import edu.tamu.weaver.auth.exception.CredentialsNotFoundException;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.utility.AnnotationUtility;

public final class WeaverCredentialsArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AnnotationUtility.findMethodAnnotation(WeaverCredentials.class, parameter) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CredentialsNotFoundException("Authentication Object Not Found");
        }
        if (!(authentication.getCredentials() instanceof Credentials)) {
            throw new CredentialsNotFoundException("Authentication Object Missing Credentials");
        }
        return authentication.getCredentials();
    }

}
