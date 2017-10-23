package edu.tamu.weaver.token.service;

import static java.util.Calendar.MINUTE;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

    private final static String TYPE_HEADER_KEY = "typ";

    private final static String TYPE_HEADER_VALUE = "JWT";

    @Value("${auth.security.jwt.secret:verysecretsecret}")
    private String secret;

    @Value("${auth.security.jwt.issuer:localhost}")
    private String issuer;

    @Value("${auth.security.jwt.duration:2}")
    private int duration;

    private Key key;

    @PostConstruct
    private void setup() {
        key = new SecretKeySpec(secret.getBytes(), "AES");
    }

    public String createToken(String subject, Map<String, Object> claims) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(MINUTE, duration);
        Date expiration = calendar.getTime();
        // @formatter:off
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setIssuer(issuer)
                .setSubject(subject)
                .setExpiration(expiration)
                .setHeaderParam(TYPE_HEADER_KEY, TYPE_HEADER_VALUE)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        // @formatter:on
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64URLSafeString(cipher.doFinal(jwt.getBytes()));
    }

    public String refreshToken(String token) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Claims claims = parseIgnoringExpiration(token);
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claims.entrySet().forEach(claim -> {
            claimsMap.put(claim.getKey(), claim.getValue());
        });
        return createToken(claims.getSubject(), claimsMap);
    }

    public Long tokenDuration(String token) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        Claims claims = parseIgnoringExpiration(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - now.getTime();
    }

    public Claims parse(String jwe) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        String jwt = new String(cipher.doFinal(Base64.decodeBase64(jwe)));
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
    }

    public Claims parseIgnoringExpiration(String token) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Claims claims;
        try {
            claims = parse(token);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
        return claims;
    }

}
