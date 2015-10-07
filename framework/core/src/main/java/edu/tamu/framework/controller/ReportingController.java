package edu.tamu.framework.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.util.EmailUtility;

@RestController
@RequestMapping("/rest")
@MessageMapping("/report")
public class ReportingController {
	
	@Autowired
	private EmailUtility emailUtility;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	
	@MessageMapping("/error")
	@RequestMapping(value = "/report/error", method = POST)
	@SendToUser
	public ApiResponse reportError(@Shib Object shibObj, @Data String data) throws Exception {

		Credentials shib = (Credentials) shibObj;
		
		Map<String, String> errorReport = new HashMap<String, String>();
		
		try {
			errorReport = objectMapper.readValue(data, new TypeReference<HashMap<String, String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		errorReport.put("user", shib.getFirstName() + " " + shib.getLastName() + " (" + shib.getUin() + ")");
		
		String content = "Error Report\n\n";
		
		content += "channel: " +  errorReport.get("channel") + "\n";
		content += "time: " +  format.format(new Date(Long.parseLong(errorReport.get("time"))*1000)) + "\n";
		content += "type: " +  errorReport.get("type") + "\n";
		content += "message: " +  errorReport.get("message") + "\n";
		content += "user: " +  errorReport.get("user") + "\n";
		
		emailUtility.sendEmail("MyLibrary Error Report", content);
		
		return new ApiResponse(SUCCESS);
	}
	
}
