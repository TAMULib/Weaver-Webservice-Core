package edu.tamu.weaver.auth.service;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import edu.tamu.weaver.auth.model.AbstractWeaverUserDetails;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.token.exception.ExpiredTokenException;
import edu.tamu.weaver.token.exception.InvalidTokenException;
import edu.tamu.weaver.token.service.TokenService;

@Service
public class TokenAuthenticationService<U extends AbstractWeaverUserDetails, R extends AbstractWeaverUserRepo<U>, S extends AbstractWeaverUserDetailsService<U, R>> {

    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationService.class);

    @Autowired
    private S userDetailsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserCredentialsService<U, R> userCredentialsService;

    public Principal authenticate(String token) {
        Map<String, String> claims = tokenService.validateJwt(token);

        String errorMessage = claims.get("ERROR");
        if (errorMessage != null) {
            LOG.error("Token error: " + errorMessage);
            throw new InvalidTokenException("INVALID", errorMessage);
        }

        if (tokenService.isExpired(claims)) {
            LOG.info("The token for " + claims.get("firstName") + " " + claims.get("lastName") + " (" + claims.get("uin") + ") has expired. Attempting to get new token.");
            throw new ExpiredTokenException("EXPIRED", "Token is expired!");
        }

        Credentials credentials = new Credentials(claims);
        U user = userCredentialsService.updateUserByCredentials(credentials);
        UserDetails userDetails = userDetailsService.buildUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}
