/* 
 * JWTtoken.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.jwt;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.framework.util.JwtUtility;

import edu.tamu.framework.model.jwt.JWTheader;
import edu.tamu.framework.model.jwt.JWTclaim;

/**
 * JSON Web Token.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class JWT {

    private JWTheader header;
    private JWTclaim claim;
    private String secret;

    /**
     * Constructor.
     *
     * @param content
     *            Map<String, String>
     * @param secret
     *            String
     *
     * @exception JsonProcessingException
     * @exception InvalidKeyException
     * @exception NoSuchAlgorithmException
     * @exception IllegalStateException
     * @exception UnsupportedEncodingException
     * 
     */
    public JWT(Map<String, String> content, String secret, Long expiration) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {

        JWTheader newHeader = new JWTheader(new HashMap<String, String>());

        JWTclaim newClaim = new JWTclaim(content);

        this.header = newHeader;
        this.claim = newClaim;
        this.secret = secret;

        makeClaim("exp", Objects.toString(Calendar.getInstance().getTime().getTime() + expiration, null));
    }

    /**
     * Constructor.
     *
     * @param secret
     *            String
     *
     * @exception JsonProcessingException
     * @exception InvalidKeyException
     * @exception NoSuchAlgorithmException
     * @exception IllegalStateException
     * @exception UnsupportedEncodingException
     * 
     */
    public JWT(String secret, Long expiration) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        this.header = new JWTheader(new HashMap<String, String>());
        this.claim = new JWTclaim(new HashMap<String, String>());
        this.secret = secret;

        makeClaim("exp", Objects.toString(Calendar.getInstance().getTime().getTime() + expiration, null));
    }

    /**
     * Add claim to token.
     *
     * @param key
     *            String
     * @param value
     *            String
     *
     */
    public void makeClaim(String key, String value) {
        this.claim.putClaim(key, value);
    }

    /**
     * Retrieve token as a String.
     *
     * @return String
     * @throws JsonProcessingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     *
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * 
     */
    public String getTokenAsString() throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        JwtUtility jwtService = new JwtUtility();

        String encodedHeader = jwtService.encodeJSON(header.getHeaderAsJSON());
        String encodedClaim = jwtService.encodeJSON(claim.getClaimAsJSON());

        String jwt = encodedHeader + "." + encodedClaim + "." + jwtService.hashSignature(encodedHeader + "." + encodedClaim, secret);

        Key key = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);

        byte[] encVal = c.doFinal(jwt.getBytes());

        String jwe = encodeBase64URLSafeString(encVal);

        return jwe;
    }

}
