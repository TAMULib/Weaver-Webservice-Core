package edu.tamu.weaver.auth;

import static edu.tamu.weaver.response.ApiStatus.REFRESH;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import edu.tamu.weaver.response.ApiResponse;

public class AuthConstants {
    
    public final static String XML_HTTP_REQUEST_HEADER = "X-Requested-With";

    public final static String AUTHORIZATION_HEADER = "jwt";

    public final static String DEFAULT_CHARSET = "UTF-8";

    public final static List<GrantedAuthority> ANONYMOUS_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

    public final static ApiResponse EXPIRED_TOKEN_RESPONSE = new ApiResponse(REFRESH);

}