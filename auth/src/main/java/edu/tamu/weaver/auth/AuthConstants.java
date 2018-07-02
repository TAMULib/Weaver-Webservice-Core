package edu.tamu.weaver.auth;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.REFRESH;
import static edu.tamu.weaver.response.ApiStatus.UNAUTHORIZED;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.response.ApiResponse;

public class AuthConstants {

    public final static String XML_HTTP_REQUEST_HEADER = "X-Requested-With";

    public final static String AUTHORIZATION_HEADER = "jwt";

    public final static String DEFAULT_CHARSET = "UTF-8";

    public final static List<GrantedAuthority> ANONYMOUS_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

    public final static byte[] EXPIRED_RESPONSE;

    public final static byte[] ERROR_RESPONSE;

    public final static byte[] UNAUTHORIZED_RESPONSE;

    public final static ApiResponse UNAUTHORIZED_API_RESPONSE = new ApiResponse(UNAUTHORIZED);

    public final static ApiResponse SERVER_ERROR_API_RESPONSE = new ApiResponse(ERROR);

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] expiredResponse = new byte[0];
        try {
            expiredResponse = objectMapper.writeValueAsBytes(new ApiResponse(REFRESH));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            EXPIRED_RESPONSE = expiredResponse;
        }

        byte[] errorResponse = new byte[0];
        try {
            errorResponse = objectMapper.writeValueAsBytes(new ApiResponse(ERROR));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            ERROR_RESPONSE = errorResponse;
        }

        byte[] unauthorizedResponse = new byte[0];
        try {
            unauthorizedResponse = objectMapper.writeValueAsBytes(UNAUTHORIZED_API_RESPONSE);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            UNAUTHORIZED_RESPONSE = unauthorizedResponse;
        }
    }

}