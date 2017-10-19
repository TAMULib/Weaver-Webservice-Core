package edu.tamu.weaver.token.service;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.token.exception.InvalidTokenException;
import edu.tamu.weaver.token.model.Token;

/**
 * JSON Web Token Service.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class TokenService {

    private final static Logger LOG = LoggerFactory.getLogger(TokenService.class);

    @Value("${auth.security.jwt.secret-key}")
    private String secretKey;

    @Value("${auth.security.jwt-expiration}")
    private Long expiration;

    @Value("${shib.keys:'netid,uin,lastName,firstName,email'}")
    private String[] shibKeys;

    @Autowired
    private Environment env;

    @Autowired
    public ObjectMapper objectMapper;

    public TokenService() {

    }

    /**
     * Instantiate new token.
     * 
     * @return
     */
    public Token craftToken() {
        try {
            return new Token(secretKey, expiration);
        } catch (Exception e) {
            LOG.debug(e.getStackTrace().toString());
            throw new InvalidTokenException("Invalid Token Exception", e.getMessage());
        }
    }

    /**
     * Get token as a string.
     * 
     * @param token
     * @return
     */
    public String tokenAsString(Token token) {
        try {
            return token.getTokenAsString();
        } catch (Exception e) {
            LOG.debug(e.getStackTrace().toString());
            throw new InvalidTokenException("Invalid Token Exception", e.getMessage());
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
    public Token makeToken(Map<String, String> payload) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        Token token = craftToken();
        for (String k : shibKeys) {
            token.makeClaim(k, payload.get(env.getProperty("shib." + k, "")) != null ? payload.get(env.getProperty("shib." + k, "")) : payload.get(k));
        }
        return token;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> validateJwt(String jwe) {

        Map<String, String> tokenMap = new HashMap<String, String>();

        if (jwe == null) {
            tokenMap.put("ERROR", "MISSING_JWT");
            return tokenMap;
        }

        Key key = new SecretKeySpec(secretKey.getBytes(), "AES");
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

        MacSigner hmac = new MacSigner(secretKey);
        Jwt token = null;

        try {
            token = JwtHelper.decodeAndVerify(new String(decValue), hmac);
        } catch (Exception e) {
            LOG.error("Invalid token! Not verified!");
            tokenMap.put("ERROR", "INVALID_JWT");
            return tokenMap;
        }

        try {
            tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
        } catch (Exception e) {
            LOG.error("Invalid token! Unable to map!");
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
        if (LOG.isDebugEnabled()) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy - hh:mm:ss");
            LOG.debug("Token expiration time: " + sdf.format(new Date(expTime)));
        }
        if (currentTime >= expTime) {
            return true;
        } else {
            Long remainingTimeInSeconds = (expTime - currentTime) / 1000;
            if (remainingTimeInSeconds > 60) {
                LOG.debug("Token expires in " + remainingTimeInSeconds / 60 + " minutes.");
            } else {
                LOG.debug("Token expires in " + remainingTimeInSeconds + " seconds.");
            }
        }
        return false;
    }

}
