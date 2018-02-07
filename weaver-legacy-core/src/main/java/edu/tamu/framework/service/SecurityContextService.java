package edu.tamu.weaver.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import edu.tamu.weaver.model.AbstractCoreUser;
import edu.tamu.weaver.model.Credentials;

@Service
public class SecurityContextService<U extends AbstractCoreUser> {

    @Autowired
    private SecurityContext securityContext;

    public void setAuthentication(String name, U user) {
        setAuthentication(name, user, user.getAuthorities());
    }

    public void setAuthentication(String name, Credentials credentials) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority(credentials.getRole()));
        setAuthentication(name, credentials, grantedAuthorities);
    }

    public void setAuthentication(String name, Object principal, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Authentication auth = new AnonymousAuthenticationToken(name, principal, grantedAuthorities);
        auth.setAuthenticated(true);
        securityContext.setAuthentication(auth);
    }

    public String getAuthenticatedName() {
        return securityContext.getAuthentication().getName();
    }

    public Object getAuthenticatedPrincipal() {
        return securityContext.getAuthentication().getPrincipal();
    }

}
