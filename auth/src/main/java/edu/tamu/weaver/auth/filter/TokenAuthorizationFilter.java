package edu.tamu.weaver.auth.filter;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.REFRESH;
import static edu.tamu.weaver.auth.AuthConstants.AUTHORIZATION_HEADER;
import static edu.tamu.weaver.auth.AuthConstants.DEFAULT_CHARSET;
import static edu.tamu.weaver.auth.AuthConstants.XML_HTTP_REQUEST_HEADER;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import edu.tamu.weaver.auth.model.AbstractWeaverUserDetails;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;
import edu.tamu.weaver.auth.service.TokenAuthenticationService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class TokenAuthorizationFilter<U extends AbstractWeaverUserDetails, R extends AbstractWeaverUserRepo<U>, S extends AbstractWeaverUserDetailsService<U, R>> extends BasicAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthorizationFilter.class);

    @Lazy
    @Autowired
    private TokenAuthenticationService<U, R, S> tokenAuthenticationService;

    @Autowired
    private JsonMapper jsonMapper;

    public TokenAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean doFilter = true;
        if (request.getHeader(XML_HTTP_REQUEST_HEADER) != null && request.getHeader(AUTHORIZATION_HEADER) != null) {
            String token = request.getHeader(AUTHORIZATION_HEADER);
            if (token != null) {
                try {
                    tokenAuthenticationService.authenticate(token);
                } catch (Exception exception) {
                    LOG.info(exception.getMessage());
                    response.setContentType(APPLICATION_JSON.toString());
                    response.setCharacterEncoding(DEFAULT_CHARSET);
                    if (exception instanceof ExpiredJwtException) {
                        // REFRESH used for expired response. Kept to preserve behavior.
                        response.getOutputStream().write(toBytes(REFRESH));
                    } else {
                        response.getOutputStream().write(toBytes(ERROR));
                    }
                    doFilter = false;
                }
            }
        }
        if (doFilter) {
            chain.doFilter(request, response);
        }
    }

    /**
     * Convert ApiStatus to byte response.
     * 
     * @param ApiStatus status.
     * 
     * @return Byte form of ApiStatus response.
     */
    private byte[] toBytes(ApiStatus status) {
        try {
            return jsonMapper.writeValueAsBytes(new ApiResponse(status));
        } catch (JacksonException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

}
