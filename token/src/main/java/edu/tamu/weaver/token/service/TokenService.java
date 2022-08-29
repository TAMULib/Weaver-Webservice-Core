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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    private final static Logger LOG = LoggerFactory.getLogger(TokenService.class);

    private final static String TYPE_HEADER_KEY = "typ";

    private final static String TYPE_HEADER_VALUE = "JWT";

    private final static String ENCRYPTION_ALGORITHM = "AES";

    @Value("${auth.security.jwt.secret:verysecretsecret}")
    private String secret;

    @Value("${auth.security.jwt.issuer:localhost}")
    private String issuer;

    @Value("${auth.security.jwt.duration:5}")
    private int duration;

    @Value("${shib.keys:netid,uin,lastName,firstName,email}")
    private String[] shibKeys;

    @Value("${shib.subject:netid}")
    private String shibSubject;

    @Autowired
    private Environment env;

    private Key jwtKey;

    private Key key;

    @PostConstruct
    private void setup() {
        jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        key = new SecretKeySpec(secret.getBytes(), ENCRYPTION_ALGORITHM);
    }

    public String createToken(Map<String, Object> claims) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return createToken("aggie", claims);
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
                .signWith(jwtKey, SignatureAlgorithm.HS512)
                .compact();
        // @formatter:on
        LOG.debug("created jwt: {}", jwt);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        String jwe = Base64.encodeBase64URLSafeString(cipher.doFinal(jwt.getBytes()));
        LOG.debug("encrypted jwt: {}",  jwe);
        return jwe;
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
        LOG.debug("parsing jwe: {}", jwe);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        String jwt = new String(cipher.doFinal(Base64.decodeBase64(jwe)));
        LOG.debug("parsed: {}", jwt);
        return Jwts.parserBuilder().setSigningKey(jwtKey).build().parseClaimsJws(jwt).getBody();
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

    public String craftToken(Map<String, String> headers) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Map<String, Object> claims = new HashMap<String, Object>();
        for (String k : shibKeys) {
            claims.put(k, headers.get(env.getProperty("shib." + k, "")) != null ? headers.get(env.getProperty("shib." + k, "")) : headers.get(k));
        }
        String subject = (String) claims.get(shibSubject);
        return createToken(subject, claims);
    }

}
