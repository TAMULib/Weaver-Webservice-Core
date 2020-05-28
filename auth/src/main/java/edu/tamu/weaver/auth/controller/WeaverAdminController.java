package edu.tamu.weaver.auth.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import edu.tamu.weaver.token.service.TokenService;
import edu.tamu.weaver.utility.HttpUtility;

/**
 * Opt-in by default.
 *
 * To disable:
 * app.admin.controller.enabled: false
 */
@RestController
@RequestMapping("/admin")
@ConditionalOnProperty(prefix = "app", name = "admin.controller.enabled", havingValue = "true", matchIfMissing = true)
public class WeaverAdminController {

    @Value("${app.authority.admins}")
    private String[] admins;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Admin endpoint. Queries LDAP with netid and returns jwt of assumed user.
     *
     * @param params
     * @RequestParam() Map<String,String>
     *
     * @return ApiResponse with token as payload
     * @throws Exception
     */
    @GetMapping
    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse admin(@RequestParam() Map<String, String> params) throws Exception {

        ApiResponse apiResponse;

        String netid = params.get("netid");

        String response = HttpUtility.makeHttpRequest("http://php.library.tamu.edu/utilities/get_person_info.php?netid=" + netid, "GET");

        Map<String, String> info = objectMapper.readValue(response, Map.class);

        if (info.get("result") == null) {

            Map<String, String> claims = new HashMap<String, String>();

            claims.put("edupersonprincipalnameunscoped", netid);

            claims.put("tamuuin", info.get("uin"));

            claims.put("tdl-sn", info.get("last_name"));
            claims.put("tdl-givenname", info.get("first_name"));
            claims.put("tdl-mail", info.get("tamu_preferred_alias"));

            String affiliation = info.get("employee_type_name");

            if ("".equals(affiliation) || "Student".equals(affiliation)) {
                String classification = info.get("classification_name").split(" ")[0].replace(",", "");
                claims.put("tdl-metadata-edupersonaffiliation", classification);
            } else {
                claims.put("tdl-metadata-edupersonaffiliation", affiliation);
            }

            apiResponse = new ApiResponse(ApiStatus.SUCCESS, "Assume token request successful.", tokenService.craftToken(claims));

        } else {
            apiResponse = new ApiResponse(ApiStatus.INVALID, "netid not found");
        }

        return apiResponse;
    }

    /**
     * Get UIN from a netid.
     * 
     * @param netid String
     * 
     * @return String
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * 
     * @throws Exception
     */
    protected String getUin(final String netid) throws IOException, ParserConfigurationException, SAXException {

        String response = HttpUtility.makeHttpRequest("http://php.library.tamu.edu/utilities/find_netid_uin.php?netid=" + netid, "GET");

        Document resDoc = convertStringToDocument(response);

        if (resDoc.getElementById("result") != null) {
            throw new RuntimeException("Please Register IP");
        }

        response = response.substring(1, response.length() - 1);
        response = response.split(",")[1];
        response = response.split(":")[1];

        return response;
    }

    /**
     * Convert string to document.
     * 
     * @param xmlStr
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private static Document convertStringToDocument(String xmlStr) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
        return doc;
    }

}
