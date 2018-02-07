/* 
 * ReportingController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.controller;

import static edu.tamu.weaver.enums.ApiResponseType.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.weaver.aspect.annotation.ApiCredentials;
import edu.tamu.weaver.aspect.annotation.ApiData;
import edu.tamu.weaver.aspect.annotation.ApiMapping;
import edu.tamu.weaver.model.ApiResponse;
import edu.tamu.weaver.model.Credentials;
import edu.tamu.weaver.util.EmailSender;

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

    @Value("${app.reporting.address}")
    private String reportingAddress;

    @Autowired
    private EmailSender emailSender;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    public ApiResponse reportError(@ApiCredentials Credentials shib, @ApiData Map<String, String> errorReport) throws Exception {

        String content = "Error Report\n\n";

        Date now = new Date();

        content += "channel: " + errorReport.get("channel") + "\n";
        content += "time: " + now + "\n";
        content += "type: " + errorReport.get("type") + "\n";
        content += "message: " + errorReport.get("message") + "\n";
        content += "user: " + shib.getFirstName() + " " + shib.getLastName() + " (" + shib.getUin() + ")" + "\n";

        emailSender.sendEmail(reportingAddress, "Error Report", content);
        logger.info(content);

        return new ApiResponse(SUCCESS, now.toString());
    }

}
