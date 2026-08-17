package edu.tamu.weaver.auth.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import tools.jackson.databind.json.JsonMapper;

import edu.tamu.weaver.auth.service.CryptoService;
import edu.tamu.weaver.email.service.EmailSender;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.token.service.TokenService;

public abstract class WeaverAuthController {

    protected final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";

    @Autowired
    protected JsonMapper jsonMapper;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected CryptoService cryptoService;

    @Autowired
    protected EmailSender emailSender;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract ApiResponse registration(@RequestBody Map<String, String> dataMap, @RequestParam("parameters") Map<String, String> parameters);

    public abstract ApiResponse login(@RequestBody Map<String, String> dataMap);

}
