/* 
 * ReportingController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.util.EmailUtility;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@RestController
@ApiMapping("/report")
public class ReportingController {

	@Autowired
	private EmailUtility emailUtility;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Report error endpoint.
	 * 
	 * @param shibObj
	 *            Object
	 * @param data
	 *            String
	 * @return ApiResponse
	 * @throws Exception
	 */
	@ApiMapping(value = "/error", method = POST)
	public ApiResponse reportError(@Shib Object shibObj, @Data String data) throws Exception {

		Credentials shib = (Credentials) shibObj;

		Map<String, String> errorReport = new HashMap<String, String>();

		try {
			errorReport = objectMapper.readValue(data, new TypeReference<HashMap<String, String>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		errorReport.put("user", shib.getFirstName() + " " + shib.getLastName() + " (" + shib.getUin() + ")");

		String content = "Error Report\n\n";

		Date now = new Date();

		content += "channel: " + errorReport.get("channel") + "\n";
		content += "time: " + now + "\n";
		content += "type: " + errorReport.get("type") + "\n";
		content += "message: " + errorReport.get("message") + "\n";
		content += "user: " + errorReport.get("user") + "\n";

		emailUtility.sendEmail("Error Report", content);

		return new ApiResponse(SUCCESS, now.toString());
	}

}
