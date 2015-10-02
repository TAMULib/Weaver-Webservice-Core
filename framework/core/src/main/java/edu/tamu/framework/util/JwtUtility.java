/* 
 * JwtUtility.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;;

/** 
 * JSON Web Token service.
 * 
 * @author
 *
 */
@Service
public class  JwtUtility {
	
	@Value("${app.security.jwt.secret_key}") 
	private String secret_key;
	
	@Autowired
	public ObjectMapper objectMapper;
	
	private static final Logger logger = Logger.getLogger(JwtUtility.class);
	
	/**
	 * Constructor.
	 *
	 */
	public JwtUtility() {
		
	}
	
	/**
	 * Encodes JSON.
	 *
	 * @param       json			String
	 *
	 * @return		String
	 *
	 */
	public String encodeJSON(String json) {
		return encodeBase64URLSafeString(json.getBytes());  
	}
	
	/**
	 * Hashes signature with secret and returns it encoded.
	 *
	 * @param       sig				String
	 * @param       secret			String
	 *
	 * @return      String
	 *
	 * @exception   NoSuchAlgorithmException
	 * @exception   InvalidKeyException
	 * 
	 */
	public String hashSignature(String sig, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
		
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		
		byte[] signature = sha256_HMAC.doFinal(sig.getBytes());
		
		return encodeBase64URLSafeString(signature); 
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> validateJWT(String jwt) {
		
		MacSigner hmac = new MacSigner(secret_key);
		Jwt token = null;
		Map<String, String> tokenMap = new HashMap<String, String>();
		
		if(jwt == null) {
			tokenMap.put("ERROR", "MISSING_JWT");
			return tokenMap;
		}
		 	
		try {
			token = JwtHelper.decodeAndVerify(jwt, hmac);
		} catch (Exception e) {
			logger.error("Invalid token! Not verified!");
			tokenMap.put("ERROR", "INVALID_JWT");
			return tokenMap;
		}
				    	
		try {
			tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
		} catch (Exception e) {
			logger.error("Invalid token! Unable to map!");
			tokenMap.put("ERROR", "INVALID_JWT");
			return tokenMap;
		}
		
		return tokenMap;
		
	}
	
	public boolean isExpired(Map<String, String> tokenMap) {
		long currentTime = Calendar.getInstance().getTime().getTime()+90000;
		
		long expTime = Long.parseLong(tokenMap.get("exp"));
		
		if(logger.isDebugEnabled()) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy - hh:mm:ss");
			logger.debug("Token expiration time: " + sdf.format(new Date(expTime)));
		}		
		
		if(expTime < currentTime) {
			return true;
		}
		else {
			Long remainingTimeInSeconds =  (expTime - currentTime)/1000;
			if(remainingTimeInSeconds > 60)
				logger.debug("Token expires in " + remainingTimeInSeconds/60  + " minutes.");
			else
				logger.debug("Token expires in " + remainingTimeInSeconds + " seconds.");
		}
		
		return false;
	}

}
