package edu.tamu.weaver.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import edu.tamu.weaver.auth.filter.TokenAuthorizationFilter;
import edu.tamu.weaver.auth.model.AbstractWeaverUserDetails;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;

public abstract class AuthWebSecurityConfig<U extends AbstractWeaverUserDetails, R extends AbstractWeaverUserRepo<U>, S extends AbstractWeaverUserDetailsService<U, R>> extends WebSecurityConfigurerAdapter {

    @Autowired
    private S userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(buildRoleHierarchy());
        return roleHierarchy;
    }

    @Bean
    public TokenAuthorizationFilter<U, R, S> tokenAuthorizationFilter() throws Exception {
        return new TokenAuthorizationFilter<U, R, S>(authenticationManager());
    }

    protected SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return defaultWebSecurityExpressionHandler;
    }

    @Override
    protected abstract void configure(HttpSecurity http) throws Exception;

    protected abstract String buildRoleHierarchy();

}