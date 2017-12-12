package edu.tamu.weaver.token.provider.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.token.service.TokenService;

@RestController
@RequestMapping("${auth.path:}")
public class TokenController {

    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

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
        redirect.setUrl(referer + "?jwt=" + tokenService.craftToken(headers));
        return redirect;
    }

    @RequestMapping("/refresh")
    public ApiResponse refresh(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        LOG.debug("Refresh token requested.");
        // NOTE: this only works with shibboleth payload in the headers
        // if not behind service provider a token can be crafted without authentication!!!!!
        return new ApiResponse(SUCCESS, "Token refresh successful.", tokenService.craftToken(headers));
    }

}
