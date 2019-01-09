package edu.tamu.weaver.auth.proxy;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.core.Authentication;

import edu.tamu.weaver.auth.model.AccessDecision;

public interface SecurityInterceptorProxy {

    public AccessDecisionManager getAccessDecisionManager();

    public SecurityMetadataSource getSecurityMetadataSource();

    public Collection<ConfigAttribute> getAttributes(Object object);
    
    public Collection<ConfigAttribute> getAllAttributes();

    public AccessDecision decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes);

}
