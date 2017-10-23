package edu.tamu.weaver.auth.filter;

import static edu.tamu.weaver.auth.AuthConstants.AUTHORIZATION_HEADER;
import static edu.tamu.weaver.auth.AuthConstants.DEFAULT_CHARSET;
import static edu.tamu.weaver.auth.AuthConstants.ERROR_RESPONSE;
import static edu.tamu.weaver.auth.AuthConstants.EXPIRED_RESPONSE;
import static edu.tamu.weaver.auth.AuthConstants.XML_HTTP_REQUEST_HEADER;
import static edu.tamu.weaver.auth.model.AccessDecision.ALLOW_ANONYMOUS;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import edu.tamu.weaver.auth.model.AbstractWeaverUserDetails;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;
import edu.tamu.weaver.auth.service.RestAccessManagerService;
import edu.tamu.weaver.auth.service.TokenAuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class TokenAuthorizationFilter<U extends AbstractWeaverUserDetails, R extends AbstractWeaverUserRepo<U>, S extends AbstractWeaverUserDetailsService<U, R>> extends BasicAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthorizationFilter.class);

    @Autowired
    private RestAccessManagerService restAccessManagerService;

    @Autowired
    private TokenAuthenticationService<U, R, S> tokenAuthenticationService;

    public TokenAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean doFilter = true;
        if (request.getHeader(XML_HTTP_REQUEST_HEADER) != null && request.getHeader(AUTHORIZATION_HEADER) != null) {
            if (restAccessManagerService.decideAccess(request) != ALLOW_ANONYMOUS) {
                String token = request.getHeader(AUTHORIZATION_HEADER);
                if (token != null) {
                    try {
                        tokenAuthenticationService.authenticate(token);
                    } catch (Exception exception) {
                        LOG.info(exception.getMessage());
                        response.setContentType(APPLICATION_JSON.toString());
                        response.setCharacterEncoding(DEFAULT_CHARSET);
                        if (exception instanceof ExpiredJwtException) {
                            response.getOutputStream().write(EXPIRED_RESPONSE);
                        } else {
                            response.getOutputStream().write(ERROR_RESPONSE);
                        }
                        doFilter = false;
                    }
                }
            }
        }
        if (doFilter) {
            chain.doFilter(request, response);
        }
    }

}
