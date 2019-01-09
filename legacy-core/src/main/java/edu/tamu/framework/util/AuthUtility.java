/* 
 * AuthUtility.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUtility {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final static String ENCRYPTION_ALGORITHM = "AES";
    private final static String RAW_DATA_DELIMETER = ":";

    @Value("${app.security.secret}")
    private String secret;

    public String generateToken(String content, String type) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Date now = new Date();
        String rawToken = now.getTime() + RAW_DATA_DELIMETER + content + RAW_DATA_DELIMETER + type;
        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), ENCRYPTION_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return Base64.encodeBase64URLSafeString(cipher.doFinal(rawToken.getBytes()));
    }

    public String[] validateToken(String token, String type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), ENCRYPTION_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return new String(cipher.doFinal(Base64.decodeBase64(token))).split(RAW_DATA_DELIMETER);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

}
