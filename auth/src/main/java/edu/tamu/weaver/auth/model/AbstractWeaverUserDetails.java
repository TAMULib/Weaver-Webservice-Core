package edu.tamu.weaver.auth.model;

import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.weaver.user.model.AbstractWeaverUser;

public abstract class AbstractWeaverUserDetails extends AbstractWeaverUser implements UserDetails {

    private static final long serialVersionUID = -547216135872810023L;

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

}
