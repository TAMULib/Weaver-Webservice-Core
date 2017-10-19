package edu.tamu.weaver.auth.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.email.service.EmailSender;
import edu.tamu.weaver.response.ApiResponse;

public abstract class WeaverAuthController {

    protected final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected EmailSender emailSender;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract ApiResponse registration(@RequestBody Map<String, String> dataMap, @RequestParam Map<String, String> parameters);

    public abstract ApiResponse login(@RequestBody Map<String, String> dataMap);

}
