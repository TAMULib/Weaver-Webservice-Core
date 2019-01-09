package edu.tamu.weaver.auth.proxy;

import static edu.tamu.weaver.auth.model.AccessDecision.ALLOW_ANONYMOUS;
import static edu.tamu.weaver.auth.model.AccessDecision.EMPTY_ATTRIBUTES;
import static edu.tamu.weaver.auth.model.AccessDecision.REQUIRES_AUTHENTICATION;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import edu.tamu.weaver.auth.model.AccessDecision;

public abstract class AbstractSecurityInterceptorProxy implements SecurityInterceptorProxy {

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        return getSecurityMetadataSource().getAttributes(object);
    }

    @Override
    public Collection<ConfigAttribute> getAllAttributes() {
        return getSecurityMetadataSource().getAllConfigAttributes();
    }

    @Override
    public AccessDecision decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        AccessDecision accessDecision = ALLOW_ANONYMOUS;
        if (attributes != null) {
            if (attributes.isEmpty()) {
                accessDecision = EMPTY_ATTRIBUTES;
            } else {
                try {
                    getAccessDecisionManager().decide(authentication, object, attributes);
                } catch (Exception e) {
                    accessDecision = REQUIRES_AUTHENTICATION;
                }
            }
        }
        return accessDecision;
    }

}
