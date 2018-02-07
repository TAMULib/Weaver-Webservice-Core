package edu.tamu.weaver.auth.service;

import static edu.tamu.weaver.auth.AuthConstants.ANONYMOUS_AUTHORITIES;
import static edu.tamu.weaver.auth.model.AccessDecision.ALLOW_ANONYMOUS;
import static edu.tamu.weaver.auth.model.AccessDecision.REQUIRES_AUTHENTICATION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import edu.tamu.weaver.auth.model.AccessDecision;

public abstract class AbstractAccessManagerService<R, S, M> implements AccessManagerService<R> {

    protected final static AnonymousAuthenticationToken ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("access", "anonymous", ANONYMOUS_AUTHORITIES);

    @Lazy
    @Autowired
    protected AbstractMethodSecurityMetadataSource abstractMethodSecurityMetadataSource;

    @Autowired
    protected S securityInterceptorProxy;

    @Autowired
    protected M mappingHandlerProxy;

    protected AccessDecision combineDecisions(AccessDecision accessDecision, AccessDecision methodAccessDecision) {
        return (accessDecision == ALLOW_ANONYMOUS && methodAccessDecision != REQUIRES_AUTHENTICATION) ? ALLOW_ANONYMOUS : REQUIRES_AUTHENTICATION;
    }

}
