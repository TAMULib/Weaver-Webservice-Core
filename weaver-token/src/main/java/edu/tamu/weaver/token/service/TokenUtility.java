package edu.tamu.weaver.token.service;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenUtility {

    /**
     * Encodes JSON.
     *
     * @param json
     *            String
     *
     * @return String
     *
     */
    public static String encodeJSON(String json) {
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
    public static String hashSignature(String sig, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] signature = sha256_HMAC.doFinal(sig.getBytes());
        return encodeBase64URLSafeString(signature);
    }

}
