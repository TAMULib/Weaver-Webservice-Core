package edu.tamu.weaver.token.provider.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.weaver.token.model.Token;
import edu.tamu.weaver.token.service.TokenService;

@RestController("${token.path:'/auth'}")
public class TokenController {

    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    protected TokenService tokenService;

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
        redirect.setUrl(referer + "?jwt=" + tokenService.makeToken(headers));
        return redirect;
    }

    @RequestMapping("/refresh")
    public Token refresh(@RequestHeader() Map<String, String> headers) throws InvalidKeyException, JsonProcessingException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
        LOG.debug("Refresh token requested.");
        return tokenService.makeToken(headers);
    }

}
