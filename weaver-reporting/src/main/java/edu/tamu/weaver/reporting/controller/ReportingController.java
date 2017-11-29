package edu.tamu.weaver.reporting.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.weaver.email.service.EmailSender;
import edu.tamu.weaver.response.ApiResponse;

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
@DependsOn("emailSender")
@RequestMapping("/report")
public class ReportingController {

    @Value("${app.reporting.address:helpdesk@library.tamu.edu}")
    private String reportingAddress;

    @Autowired
    private EmailSender emailSender;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/error", method = POST)
    public ApiResponse reportError(@RequestBody Map<String, String> errorReport) throws Exception {

        // @ApiCredentials Credentials shib <- possibly deprecate anduse UserDetails
        String content = "Error Report\n\n";

        Date now = new Date();

        content += "channel: " + errorReport.get("channel") + "\n";
        content += "time: " + now + "\n";
        content += "type: " + errorReport.get("type") + "\n";
        content += "message: " + errorReport.get("message") + "\n";
        // content += "user: " + shib.getFirstName() + " " + shib.getLastName() + " (" + shib.getUin() + ")" + "\n";

        emailSender.sendEmail(reportingAddress, "Error Report", content);
        logger.info(content);

        return new ApiResponse(SUCCESS, now.toString());
    }

}
