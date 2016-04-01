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

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.exception.JWTException;
import edu.tamu.framework.model.jwt.JWT;;

/**
 * JSON Web Token Utility.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class JwtUtility {

	@Value("${auth.security.jwt.secret-key}")
	private String secret_key;

	@Value("${auth.security.jwt-expiration}")
	private Long expiration;

	@Autowired
	public ObjectMapper objectMapper;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public JwtUtility() { }

	/**
	 * Instantiate new token.
	 * 
	 * @return
	 */
	public JWT craftToken() {
		try {
			return new JWT(secret_key, expiration);
		} catch (InvalidKeyException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("InvalidKeyException", e.getMessage());
		} catch (JsonProcessingException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("JsonProcessingException", e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("NoSuchAlgorithmException", e.getMessage());
		} catch (IllegalStateException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("IllegalStateException", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("UnsupportedEncodingException", e.getMessage());
		}
	}

	/**
	 * Get token as a string.
	 * 
	 * @param token
	 * @return
	 */
	public String tokenAsString(JWT token) {
		try {
			return token.getTokenAsString();
		} catch (InvalidKeyException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("InvalidKeyException", e.getMessage());
		} catch (JsonProcessingException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("JsonProcessingException", e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("NoSuchAlgorithmException", e.getMessage());
		} catch (NoSuchPaddingException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("NoSuchPaddingException", e.getMessage());
		} catch (IllegalBlockSizeException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("IllegalBlockSizeException", e.getMessage());
		} catch (BadPaddingException e) {
			log.debug(e.getStackTrace().toString());
			throw new JWTException("BadPaddingException", e.getMessage());
		}
	}

	/**
	 * Make token from map populated by the user through basic login.
	 * 
	 * @param payload
	 *            Map<String, String>
	 * @return JWT
	 * @throws InvalidKeyException
	 * @throws JsonProcessingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 */
	public JWT makeToken(Map<String, String> payload) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		JWT token = craftToken();
		token.makeClaim("lastName", payload.get("lastName"));
		token.makeClaim("firstName", payload.get("firstName"));
		token.makeClaim("netid", payload.get("netid"));
		token.makeClaim("uin", payload.get("uin"));
		token.makeClaim("email", payload.get("email"));
		return token;
	}

	/**
	 * Encodes JSON.
	 *
	 * @param json
	 *            String
	 *
	 * @return String
	 *
	 */
	public String encodeJSON(String json) {
		return encodeBase64URLSafeString(json.getBytes());
	}

	/**
	 * Hashes signature with secret and returns it encoded.
	 *
	 * @param sig
	 *            String
	 * @param secret
	 *            String
	 *
	 * @return String
	 *
	 * @exception NoSuchAlgorithmException
	 * @exception InvalidKeyException
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

		if (jwe == null) {
			tokenMap.put("ERROR", "MISSING_JWT");
			return tokenMap;
		}

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
			log.error("Invalid token! Not verified!");
			tokenMap.put("ERROR", "INVALID_JWT");
			return tokenMap;
		}

		try {
			tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
		} catch (Exception e) {
			log.error("Invalid token! Unable to map!");
			tokenMap.put("ERROR", "INVALID_JWT");
			return tokenMap;
		}

		return tokenMap;

	}

	/**
     * Check if token has expired.
     * 
     * @param tokenMap
     *            Map<String, String>
     * @return
     */
    public boolean isExpired(Map<String, String> tokenMap) {
        long currentTime = new Date().getTime();
        long expTime = Long.parseLong(tokenMap.get("exp"));
        if (log.isDebugEnabled()) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy - hh:mm:ss");
            log.debug("Token expiration time: " + sdf.format(new Date(expTime)));
        }
        if (currentTime >= expTime) {
            return true;
        } else {
            Long remainingTimeInSeconds = (expTime - currentTime) / 1000;
            if (remainingTimeInSeconds > 60) {
                log.debug("Token expires in " + remainingTimeInSeconds / 60 + " minutes.");
            } else {
                log.debug("Token expires in " + remainingTimeInSeconds + " seconds.");
            }
        }
        return false;
    }

}
