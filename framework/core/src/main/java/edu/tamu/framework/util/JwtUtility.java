/* 
 * JWTservice.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
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
	public Map<String, String> validateJWT(String jwe) {
		
		Map<String, String> tokenMap = new HashMap<String, String>();
		
		if(jwe == null) {
			tokenMap.put("ERROR", "MISSING_JWT");
			return tokenMap;
		}
		
		System.out.println("Decrypting jwe!");
		
		Key key = new SecretKeySpec(secret_key.getBytes(), "AES");
        Cipher c = null;
        byte[] decordedValue = decodeBase64(jwe);
        byte[] decValue = null;

        try {
			c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, key);
			decValue = c.doFinal(decordedValue);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e1) {
			System.out.println("Could not decrypt token!" + e1);
			tokenMap.put("ERROR", "UNDECRYPTED_JWT");
			return tokenMap;
		}
        	    
		MacSigner hmac = new MacSigner(secret_key);
		Jwt token = null;
		
		try {
			token = JwtHelper.decodeAndVerify(new String(decValue), hmac);
		} catch (Exception e) {
			System.out.println("Invalid token! Not verified!");
			tokenMap.put("ERROR", "INVALID_JWT");
			return tokenMap;
		}
				    	
		try {
			tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
		} catch (Exception e) {
			System.out.println("Invalid token! Unable to map!");
			tokenMap.put("ERROR", "INVALID_JWT");
			return tokenMap;
		}
		
		return tokenMap;
		
	}
	
	public boolean isExpired(Map<String, String> tokenMap) {
		long currentTime = Calendar.getInstance().getTime().getTime()+90000;
		
		System.out.println(tokenMap.get("exp"));
		
		long expTime = Long.parseLong(tokenMap.get("exp"));
		
		if(expTime < currentTime) {
			System.out.println("Token expired!");
			return true;
		}
		else {
			System.out.println("Token expires in " + ((expTime - currentTime))/1000 + " seconds");
		}
		
		return false;
	}

}
