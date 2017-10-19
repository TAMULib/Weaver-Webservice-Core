package edu.tamu.weaver.auth.service;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    public final static String CIPHER_ALGORITHM = "AES";

    public final static String RAW_DATA_DELIMETER = ":";

    @Value("${auth.secret:'verysecretsecret'}")
    private String secret;

    @Lazy
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Cipher decryptor;

    private Cipher encryptor;

    @PostConstruct
    private void setup() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Key cipherKey = new SecretKeySpec(secret.getBytes(), CIPHER_ALGORITHM);
        decryptor = Cipher.getInstance(CIPHER_ALGORITHM);
        decryptor.init(DECRYPT_MODE, cipherKey);
        encryptor = Cipher.getInstance(CIPHER_ALGORITHM);
        encryptor.init(ENCRYPT_MODE, cipherKey);
    }

    public String generateGenericToken(String content, String type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Date now = new Date();
        String rawToken = now.getTime() + RAW_DATA_DELIMETER + content + RAW_DATA_DELIMETER + type;
        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), CIPHER_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(ENCRYPT_MODE, skeySpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(rawToken.getBytes()));
    }

    public String[] validateGenericToken(String token, String type) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(secret.getBytes(), CIPHER_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(DECRYPT_MODE, skeySpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(token))).split(RAW_DATA_DELIMETER);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

}