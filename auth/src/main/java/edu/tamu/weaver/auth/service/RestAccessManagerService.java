package edu.tamu.weaver.auth.service;

import static edu.tamu.weaver.auth.model.AccessDecision.REQUIRES_AUTHENTICATION;

import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

import edu.tamu.weaver.auth.model.AccessDecision;
import edu.tamu.weaver.auth.proxy.FilterSecurityInterceptorProxy;
import edu.tamu.weaver.auth.proxy.RequestMappingHandlerProxy;

@Service
public class RestAccessManagerService extends AbstractAccessManagerService<HttpServletRequest, FilterSecurityInterceptorProxy, RequestMappingHandlerProxy> {

    @Override
    public AccessDecision decideAccess(HttpServletRequest request) {
        FilterInvocation filterInvocation = new FilterInvocation(request.getServletPath(), request.getMethod());
        Collection<ConfigAttribute> attributes = securityInterceptorProxy.getAttributes(filterInvocation);
        AccessDecision accessDecision = securityInterceptorProxy.decide(ANONYMOUS_AUTHENTICATION, filterInvocation, attributes);
        if (accessDecision != REQUIRES_AUTHENTICATION) {
            Optional<HandlerMethod> handler = mappingHandlerProxy.getHandler(request);
            if (handler.isPresent()) {
                attributes = abstractMethodSecurityMetadataSource.getAttributes(handler.get().getMethod(), handler.get().getMethod().getDeclaringClass());
                AccessDecision methodAccessDecision = securityInterceptorProxy.decide(ANONYMOUS_AUTHENTICATION, filterInvocation, attributes);
                accessDecision = combineDecisions(accessDecision, methodAccessDecision);
            }
        }
        return accessDecision;
    }

    public String getPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    public Optional<HandlerMethod> getHandler(HttpServletRequest request) {
        return mappingHandlerProxy.getHandler(request);
    }

}
