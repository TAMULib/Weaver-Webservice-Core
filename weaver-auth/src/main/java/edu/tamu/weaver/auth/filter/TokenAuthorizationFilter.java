package edu.tamu.weaver.auth.filter;

import static edu.tamu.weaver.auth.AuthConstants.AUTHORIZATION_HEADER;
import static edu.tamu.weaver.auth.AuthConstants.DEFAULT_CHARSET;
import static edu.tamu.weaver.auth.AuthConstants.XML_HTTP_REQUEST_HEADER;
import static edu.tamu.weaver.auth.model.AccessDecision.ALLOW_ANONYMOUS;
import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.REFRESH;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;
import edu.tamu.weaver.auth.service.RestAccessManagerService;
import edu.tamu.weaver.auth.service.TokenAuthenticationService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.token.exception.ExpiredTokenException;
import edu.tamu.weaver.token.exception.TokenException;
import edu.tamu.weaver.user.model.AbstractWeaverUser;

@Component
public class TokenAuthorizationFilter<U extends AbstractWeaverUser, R extends AbstractWeaverUserRepo<U>, S extends AbstractWeaverUserDetailsService<U, R>> extends BasicAuthenticationFilter {

    private final static byte[] EXPIRED_RESPONSE;

    private final static byte[] ERROR_RESPONSE;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] expiredResponse = new byte[0];
        try {
            expiredResponse = objectMapper.writeValueAsBytes(new ApiResponse(REFRESH));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            EXPIRED_RESPONSE = expiredResponse;
        }

        byte[] errorResponse = new byte[0];
        try {
            errorResponse = objectMapper.writeValueAsBytes(new ApiResponse(ERROR));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            ERROR_RESPONSE = errorResponse;
        }
    }

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
                    } catch (TokenException exception) {
                        response.setContentType(APPLICATION_JSON.toString());
                        response.setCharacterEncoding(DEFAULT_CHARSET);
                        if (exception instanceof ExpiredTokenException) {
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
