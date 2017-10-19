package edu.tamu.weaver.auth.proxy;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Service;

@Service
public class FilterSecurityInterceptorProxy extends AbstractSecurityInterceptorProxy {

    @Lazy
    @Autowired
    private FilterChainProxy filterChainProxy;

    private FilterSecurityInterceptor filterSecurityInterceptor;

    private FilterSecurityInterceptor getFilterSecurityInterceptor() {
        if (filterSecurityInterceptor == null) {
            filterSecurityInterceptor = findFilterSecurityInterceptor();
        }
        return filterSecurityInterceptor;
    }

    @Override
    public AccessDecisionManager getAccessDecisionManager() {
        return getFilterSecurityInterceptor().getAccessDecisionManager();
    }

    @Override
    public SecurityMetadataSource getSecurityMetadataSource() {
        return getFilterSecurityInterceptor().obtainSecurityMetadataSource();
    }

    private FilterSecurityInterceptor findFilterSecurityInterceptor() {
        for (SecurityFilterChain securityFilterChain : filterChainProxy.getFilterChains()) {
            for (Filter filter : securityFilterChain.getFilters()) {
                if (filter instanceof FilterSecurityInterceptor) {
                    return (FilterSecurityInterceptor) filter;
                }
            }
        }
        throw new RuntimeException("Unable to find filter security interceptor!");
    }

}
