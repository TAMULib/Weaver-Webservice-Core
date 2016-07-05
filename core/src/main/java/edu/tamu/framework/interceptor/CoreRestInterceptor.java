/* 
 * CoreRestInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.interceptor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import edu.tamu.framework.exception.JWTException;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.HttpRequest;
import edu.tamu.framework.service.HttpRequestService;
import edu.tamu.framework.util.JwtUtility;

/**
 * REST interceptor. Intercepts AJAX request to decode and verify token before
 * allowing controller to process request.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Component
public abstract class CoreRestInterceptor extends HandlerInterceptorAdapter {

    @Value("${app.whitelist}")
    private String[] whitelist;

    @Autowired
    private JwtUtility jwtService;

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private SecurityContext securityContext;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CoreRestInterceptor() {}

    public abstract Credentials getAnonymousCredentials();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> credentialMap = new HashMap<String, String>();

        String jwt = request.getHeader("jwt");

        Credentials credentials = null;

        if (jwt == null) {

            String ip = request.getHeader("X-FORWARDED-FOR");

            if (ip == null) {
                ip = request.getRemoteAddr();
            }

            logger.debug("Referrer: " + ip);

            if (logger.isDebugEnabled()) {
                Enumeration<String> headers = request.getHeaderNames();
                while (headers.hasMoreElements()) {
                    String key = (String) headers.nextElement();
                    logger.debug(key + ": " + request.getHeader(key));
                }
            }

            if (ip == null) {
                ip = request.getRemoteAddr();
            }

            boolean accepted = false;

            for (String accept : whitelist) {
                if (ip.equals(accept)) {
                    credentialMap.put("lastName", "Admin");
                    credentialMap.put("firstName", "Server");
                    credentialMap.put("netid", ip);
                    credentialMap.put("affiliation", "Server");
                    credentialMap.put("uin", "123456789");
                    credentialMap.put("exp", "1436982214754");
                    credentialMap.put("email", "helpdesk@mailinator.com");
                    credentialMap.put("role", "ROLE_ADMIN");
                    accepted = true;
                    credentials = new Credentials(credentialMap);
                    break;
                }
            }

            if (!accepted) {
                credentials = getAnonymousCredentials();
            }
        } else {
            credentialMap = jwtService.validateJWT(request.getHeader("jwt"));

            if (logger.isDebugEnabled()) {
                Enumeration<String> headers = request.getHeaderNames();
                while (headers.hasMoreElements()) {
                    String key = (String) headers.nextElement();
                    logger.debug(key + ": " + request.getHeader(key));
                }

                logger.debug("Credential Map");
                for (String key : credentialMap.keySet()) {
                    logger.debug(key + " - " + credentialMap.get(key));
                }
            }

            String errorMessage = credentialMap.get("ERROR");
            if (errorMessage != null) {
                logger.error("JWT error: " + errorMessage);
                throw new JWTException("INVALID_JWT", errorMessage);
            }

            if (jwtService.isExpired(credentialMap)) {
                logger.info("The token for " + credentialMap.get("firstName") + " " + credentialMap.get("lastName") + " (" + credentialMap.get("uin") + ") has expired. Attempting to get new token.");
                throw new JWTException("EXPIRED_JWT", "JWT is expired!");
            }

            credentials = confirmCreateUser(new Credentials(credentialMap));

            if (credentials == null) {
                errorMessage = "Could not confirm user!";
                logger.error(errorMessage);
                throw new JWTException("INVALID_USER", errorMessage);
            }
        }

        request.setAttribute("data", request.getHeader("data"));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        grantedAuthorities.add(new SimpleGrantedAuthority(credentials.getRole()));

        if (credentials.getNetid() == null) {
            credentials.setNetid(credentials.getEmail());
        }

        Authentication auth = new AnonymousAuthenticationToken(credentials.getUin(), credentials.getNetid(), grantedAuthorities);

        auth.setAuthenticated(true);

        securityContext.setAuthentication(auth);

        httpRequestService.addRequest(new HttpRequest(request, response, credentials.getNetid(), request.getRequestURI(), credentials));

        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public abstract Credentials confirmCreateUser(Credentials shib);
}
