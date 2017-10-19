package edu.tamu.weaver.admin.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.weaver.token.service.TokenService;
import edu.tamu.weaver.utility.HttpUtility;

@RestController
public class AdminController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.authority.admins}")
    private String[] admins;

    /**
     * Admin endpoint. Checks if user uin is an admin. Queries LDAP with netid and returns jwt of assumed user.
     * 
     * @param params
     * @param headers
     * @return
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    @RequestMapping("/admin")
    @SuppressWarnings("unchecked")
    public Map<String, Object> admin(@RequestParam() Map<String, String> params, @RequestHeader() Map<String, String> headers) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException {

        boolean isAdmin = false;
        String uin = headers.get("tamuuin");
        for (String a : admins) {
            if (uin.equals(a)) {
                isAdmin = true;
                break;
            }
        }

        Map<String, Object> assumedJwt = new HashMap<String, Object>();

        if (!isAdmin) {
            assumedJwt.put("forbidden", null);
            return assumedJwt;
        }

        String netid = params.get("netid");

        Map<String, String> creds = new HashMap<String, String>();

        String response = HttpUtility.makeHttpRequest("http://php.library.tamu.edu/utilities/get_person_info.php?netid=" + netid, "GET");

        Map<String, String> info = objectMapper.readValue(response, Map.class);

        if (info.get("result") != null) {
            assumedJwt.put("invalid", "netid not found");
            return assumedJwt;
        }

        creds.put("edupersonprincipalnameunscoped", netid);

        creds.put("tamuuin", info.get("uin"));

        creds.put("tdl-sn", info.get("last_name"));
        creds.put("tdl-givenname", info.get("first_name"));
        creds.put("tdl-mail", info.get("tamu_preferred_alias"));

        String affiliation = info.get("employee_type_name");

        if ("".equals(affiliation) || "Student".equals(affiliation)) {
            String classification = info.get("classification_name").split(" ")[0].replace(",", "");
            creds.put("tdl-metadata-edupersonaffiliation", classification);
        } else {
            creds.put("tdl-metadata-edupersonaffiliation", affiliation);
        }
        assumedJwt.put("assumed", tokenService.makeToken(creds));
        return assumedJwt;
    }

    /**
     * Get uin from netid.
     * 
     * @param netid
     * @return
     * @throws Exception
     */
    protected String getUin(final String netid) throws Exception {
        String res = HttpUtility.makeHttpRequest("http://php.library.tamu.edu/utilities/find_netid_uin.php?netid=" + netid, "GET");
        res = res.substring(1, res.length() - 1);
        res = res.split(",")[1];
        res = res.split(":")[1];
        return res;
    }

}
