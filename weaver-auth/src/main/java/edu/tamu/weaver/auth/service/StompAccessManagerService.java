package edu.tamu.weaver.auth.service;

import static edu.tamu.weaver.auth.model.AccessDecision.REQUIRES_AUTHENTICATION;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Service;

import edu.tamu.weaver.auth.model.AccessDecision;
import edu.tamu.weaver.auth.proxy.ChannelSecurityInterceptorProxy;
import edu.tamu.weaver.auth.proxy.SimpAnnotationMappingHandlerProxy;

@Service
public class StompAccessManagerService extends AbstractAccessManagerService<Message<?>, ChannelSecurityInterceptorProxy, SimpAnnotationMappingHandlerProxy> {

    @Override
    public AccessDecision decideAccess(Message<?> message) {
        Collection<ConfigAttribute> attributes = securityInterceptorProxy.getAttributes(message);
        AccessDecision accessDecision = securityInterceptorProxy.decide(ANONYMOUS_AUTHENTICATION, message, attributes);
        if (accessDecision != REQUIRES_AUTHENTICATION) {
            Optional<HandlerMethod> handler = mappingHandlerProxy.getHandler(message);
            if (handler.isPresent()) {
                attributes = abstractMethodSecurityMetadataSource.getAttributes(handler.get().getMethod(), handler.get().getMethod().getDeclaringClass());
                AccessDecision methodAccessDecision = securityInterceptorProxy.decide(ANONYMOUS_AUTHENTICATION, message, attributes);
                accessDecision = combineDecisions(accessDecision, methodAccessDecision);
            }
        }
        return accessDecision;
    }

    public String getPath(StompHeaderAccessor accessor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return mappingHandlerProxy.getPath(accessor);
    }

    public Optional<HandlerMethod> findHandler(String path) {
        return mappingHandlerProxy.findHandler(path);
    }

}
