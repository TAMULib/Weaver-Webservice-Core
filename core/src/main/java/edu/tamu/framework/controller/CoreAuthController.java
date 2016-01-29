package edu.tamu.framework.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Parameters;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.util.AuthUtility;
import edu.tamu.framework.util.EmailUtility;
import edu.tamu.framework.util.JwtUtility;

public abstract class CoreAuthController {

	protected final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected AuthUtility authUtility;

	@Autowired
	protected JwtUtility jwtUtility;

	@Autowired
	protected EmailUtility emailUtility;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract ApiResponse registration(@Data String data, @Parameters Map<String, String[]> parameters);

	public abstract ApiResponse login(@Data String data);

}
