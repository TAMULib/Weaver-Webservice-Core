/* 
 * RestInterceptor.java 
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.HttpRequest;
import edu.tamu.framework.service.HttpRequestService;
import edu.tamu.framework.util.JwtUtility;

/**
 * REST interceptor. Intercepts AJAX request to decode and 
 * verify token before allowing controller to process request.
 * 
 * @author 
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
	
	private static Credentials anonymousShib;
	
	private List<String> currentUsers = new ArrayList<String>();
	
	private static final Logger logger = Logger.getLogger(CoreRestInterceptor.class);
	
	public CoreRestInterceptor() {
		anonymousShib = new Credentials();
		anonymousShib.setAffiliation("NA");
		anonymousShib.setLastName("Anonymous");
		anonymousShib.setFirstName("Role");
		anonymousShib.setNetid("anonymous");
		anonymousShib.setUin("000000000");
		anonymousShib.setExp("1436982214754");
		anonymousShib.setEmail("helpdesk@library.tamu.edu");
		anonymousShib.setRole( "ROLE_ANONYMOUS");
	}
	
	/**
	 * Handle request to decode and verify. Return error or continue to controller.
	 * 
	 * @param		request			HttpServletRequest
	 * @param		response		HttpServletResponse
	 * @param		handler			Object
	 * 
	 * @return		boolean
	 * 
	 * @exception	Exception
	 */
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {		
		Map<String, String> credentialMap = new HashMap<String, String>();
				
		String jwt = request.getHeader("jwt");
		
		Credentials shib = null;
		
		if(jwt == null) {
			
			String ip = request.getHeader("X-FORWARDED-FOR");
			
			if(ip == null) {
				ip = request.getRemoteAddr();
			}
			
			logger.debug("Referrer: " + ip);			
			
			if(logger.isDebugEnabled()) {
				Enumeration<String> headers = request.getHeaderNames();
				while(headers.hasMoreElements()) {
					String key = (String) headers.nextElement();
					logger.debug(key + ": " + request.getHeader(key));
				}
			}
			
			if (ip == null) {
				ip = request.getRemoteAddr();
			}
			
			boolean accepted = false;
			
			for(String accept : whitelist) {
				if(ip.equals(accept)) {					
					credentialMap.put("lastName", "Admin");
					credentialMap.put("firstName", "Server");
					credentialMap.put("netid", ip);
					credentialMap.put("affiliation", "Server");
					credentialMap.put("uin", "123456789");
					credentialMap.put("exp", "1436982214754");
					credentialMap.put("email", "helpdesk@library.tamu.edu");
					credentialMap.put("role", "ROLE_ADMIN");
					accepted = true;
					shib = new Credentials(credentialMap);
					break;
				}
			}
			
			if(!accepted) {
				shib = anonymousShib;
			}
		}
		else {
			credentialMap = jwtService.validateJWT(request.getHeader("jwt"));
			
			if(logger.isDebugEnabled()) {
				Enumeration<String> headers = request.getHeaderNames();
				while(headers.hasMoreElements()) {
					String key = (String) headers.nextElement();
					logger.debug(key + ": "+request.getHeader(key));
				}
				
				logger.debug("Credential Map");
				for(String key : credentialMap.keySet()) {
					logger.debug(key+" - "+credentialMap.get(key));
				}
			}
			
			String error = credentialMap.get("ERROR"); 
	    	if(error != null) {	    		
	    		logger.error("JWT error: " + error);	    		
	    		throw new InvalidJwtException();
	    	}
	    	
	    	if(jwtService.isExpired(credentialMap)) {
	    		logger.info("The token for "+credentialMap.get("firstName")+" "+credentialMap.get("lastName")+" ("+credentialMap.get("uin")+") has expired. Attempting to get new token.");
				throw new ExpiredJwtException();		
			}
	    	
	    	shib = confirmCreateUser(new Credentials(credentialMap));
		}		
		
		
		request.setAttribute("shib", shib);
		
		request.setAttribute("data", request.getHeader("data"));
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		
		grantedAuthorities.add(new SimpleGrantedAuthority(shib.getRole()));
		
		if(("ROLE_ANONYMOUS").equals(shib.getRole())) {
			shib.setNetid(shib.getNetid() + "-" + currentUsers.size());			
		}

		currentUsers.add(shib.getNetid());

		Authentication auth = new AnonymousAuthenticationToken(shib.getUin(), shib.getNetid(), grantedAuthorities);

		auth.setAuthenticated(true);

		securityContext.setAuthentication(auth);
		
		httpRequestService.addRequest(new HttpRequest(request, response, shib.getNetid(), ((HandlerMethod) handler).getBeanType().getAnnotation(ApiMapping.class).value()[0] + ((HandlerMethod) handler).getMethodAnnotation(ApiMapping.class).value()[0]));

        return true;
    }
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		logger.debug(securityContext.getAuthentication().getName() + " has finished their http request.");
		currentUsers.remove(securityContext.getAuthentication().getName());
		logger.debug("There are now " + currentUsers.size() + " users making http requests.");
	}
    
	
	/**
	 * Expired JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="EXPIRED_JWT") 
	public class ExpiredJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Missing JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="MISSING_JWT") 
	public class MissingJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Invalid JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="INVALID_JWT") 
	public class InvalidJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	public abstract Credentials confirmCreateUser(Credentials shib);
}
