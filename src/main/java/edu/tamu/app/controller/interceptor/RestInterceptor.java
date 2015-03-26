/* 
 * RestInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller.interceptor;

import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.Credentials;

/**
 * REST interceptor. Intercepts AJAX request to decode and 
 * verify token before allowing controller to process request.
 * 
 * @author 
 *
 */
@Component
public class RestInterceptor extends HandlerInterceptorAdapter {

	@Value("${app.security.jwt.secret_key}")
	private String secret_key;
	
	@Value("${app.authority.admins}")
	private String[] admins;
	
	@Autowired
	public ObjectMapper objectMapper;
	
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
       				
		MacSigner hmac = new MacSigner(secret_key); 
		String tokenString = request.getHeader("jwt");
		if(tokenString == null) throw new MissingJwtException();
		
		try {
			JwtHelper.decodeAndVerify(request.getHeader("jwt"), hmac);
		} catch (Exception e) {
			throw new InvalidJwtException();
		}
		
		Jwt token = JwtHelper.decodeAndVerify(request.getHeader("jwt"), hmac);
		
		@SuppressWarnings("unchecked")
		Map<String, String> tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
		//todo: incorporate user model
		Credentials shib = new Credentials(tokenMap);
		String shibUin = shib.getUin();
		for(String uin : admins) {
			if(uin.equals(shibUin)) {
				shib.setRole("ROLE_ADMIN");
			}
		}
		
		long currentTime = Calendar.getInstance().getTime().getTime()+90000;
		long expTime = Long.parseLong(shib.getExp());
		Boolean tokenIsExpired = expTime < currentTime;

		System.out.println("It is " + tokenIsExpired + " that the token has expired. Life in seconds: " + ((expTime - currentTime))/1000);
	
		if(tokenIsExpired) throw new ExpiredJwtException();
	
		request.setAttribute("shib", shib);
		
        return true;
    }
	
	/**
	 * Expired JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="expired") 
	public class ExpiredJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Missing JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="missing") 
	public class MissingJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Invalid JWT Exception class.
	 * 
	 * @author 
	 *
	 */
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="invalid") 
	public class InvalidJwtException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
}
