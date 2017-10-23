package edu.tamu.weaver.token.provider.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import edu.tamu.weaver.token.service.TokenService;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @Value("${shib.keys:netid,uin,lastName,firstName,email}")
    private String[] shibKeys;

    @Value("${shib.subject:netid}")
    private String shibSubject;

    @Autowired
    private Environment env;

    @Autowired
    protected TokenService tokenService;

    @RequestMapping("/token")
    public RedirectView token(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        LOG.debug("params: " + params);
        String referer = params.get("referer");
        if (referer == null) {
            LOG.debug("No referer in params!!");
            throw new RuntimeException("No referer in params!!");
        }
        RedirectView redirect = new RedirectView();
        redirect.setContextRelative(false);
        redirect.setUrl(referer + "?jwt=" + craftToken(headers));
        return redirect;
    }

    @RequestMapping("/refresh")
    public String refresh(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        LOG.debug("Refresh token requested.");
        LOG.warn("Crafting token from headers! Ensure refresh end point is behind shibboleth!");
        // NOTE: this only works with shibboleth payload in the headers
        // if not behind service provider a token can be crafted without authentication!!!!!
        return craftToken(headers);
    }

    protected String craftToken(Map<String, String> headers) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Map<String, Object> claims = new HashMap<String, Object>();
        for (String k : shibKeys) {
            claims.put(k, headers.get(env.getProperty("shib." + k, "")) != null ? headers.get(env.getProperty("shib." + k, "")) : headers.get(k));
        }
        String subject = (String) claims.get(shibSubject);
        return tokenService.createToken(subject, claims);
    }

}
