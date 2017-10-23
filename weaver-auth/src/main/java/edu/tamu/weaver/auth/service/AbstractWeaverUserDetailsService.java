package edu.tamu.weaver.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.tamu.weaver.auth.model.AbstractWeaverUserDetails;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;

public abstract class AbstractWeaverUserDetailsService<U extends AbstractWeaverUserDetails, R extends AbstractWeaverUserRepo<U>> implements UserDetailsService {

    @Autowired
    protected R userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<U> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            return buildUserDetails(user.get());
        }
        throw new UsernameNotFoundException(System.out.format("User with username %s not found!\n", username).toString());
    }

    public abstract UserDetails buildUserDetails(U user);

}