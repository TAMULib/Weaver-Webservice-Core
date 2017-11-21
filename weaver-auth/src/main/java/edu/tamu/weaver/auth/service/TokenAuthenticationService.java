package edu.tamu.weaver.auth.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import edu.tamu.weaver.auth.model.AbstractWeaverUserDetails;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.token.service.TokenService;
import io.jsonwebtoken.Claims;

@Service
public class TokenAuthenticationService<U extends AbstractWeaverUserDetails, R extends AbstractWeaverUserRepo<U>, S extends AbstractWeaverUserDetailsService<U, R>> {

    @Lazy
    @Autowired
    private S userDetailsService;

    @Lazy
    @Autowired
    private TokenService tokenService;

    @Lazy
    @Autowired
    private UserCredentialsService<U, R> userCredentialsService;

    public Principal authenticate(String token) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Claims claims = tokenService.parse(token);
        Credentials credentials = new Credentials(claims);
        U user = userCredentialsService.updateUserByCredentials(credentials);
        UserDetails userDetails = userDetailsService.buildUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}
