package edu.tamu.weaver.token.provider.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class WeaverMockTokenController extends TokenController {

    private static final Logger LOG = Logger.getLogger(WeaverMockTokenController.class);

    private static final Map<String, Map<String, String>> MOCK_CLAIMS = new HashMap<String, Map<String, String>>();

    static {
        Map<String, String> mockAdminClaims = new HashMap<String, String>();
        mockAdminClaims.put("lastName", "Daniels");
        mockAdminClaims.put("firstName", "Jack");
        mockAdminClaims.put("netid", "aggieJack");
        mockAdminClaims.put("uin", "123456789");
        mockAdminClaims.put("email", "aggieJack@tamu.edu");
        mockAdminClaims.put("role", "ROLE_ADMIN");

        MOCK_CLAIMS.put("admin", mockAdminClaims);

        Map<String, String> mockUserClaims = new HashMap<String, String>();
        mockUserClaims.put("lastName", "Boring");
        mockUserClaims.put("firstName", "Bob");
        mockUserClaims.put("netid", "bobBoring");
        mockUserClaims.put("uin", "bobBoring");
        mockUserClaims.put("email", "bobBoring@tamu.edu");
        mockUserClaims.put("role", "ROLE_USER");

        MOCK_CLAIMS.put("user", mockUserClaims);
    }

    @Override
    @RequestMapping("/token")
    public RedirectView token(@RequestParam() Map<String, String> params, @RequestHeader() Map<String, String> headers) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        LOG.debug("params: " + params);
        String referer = params.get("referer");
        if (referer == null) {
            LOG.debug("No referer in params!!");
            throw new RuntimeException("No referer in params!!");
        }
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(false);
        String mock = params.get("mock");
        if (mock == null) {
            mock = "user";
        }
        Map<String, String> claims = MOCK_CLAIMS.get(mock);
        redirect.setUrl(referer + "?jwt=" + tokenService.makeToken(claims));
        return redirect;
    }

    protected void setMockClaims(String mock, Map<String, String> claims) {
        MOCK_CLAIMS.put(mock, claims);
    }

}