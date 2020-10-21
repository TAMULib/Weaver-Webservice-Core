package edu.tamu.weaver.auth.controller;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import edu.tamu.weaver.token.service.TokenService;
import edu.tamu.weaver.utility.HttpUtility;

/**
 * Opt-in by default.
 *
 * To disable: app.assume.enabled: false
 */
@RestController
@RequestMapping("/assume")
@ConditionalOnProperty(prefix = "app", name = "assume.enabled", havingValue = "true", matchIfMissing = false)
public class WeaverAssumeUserController {

    @Value("${app.authority.admins}")
    private String[] admins;

    @Value("${app.assume.claims-url}")
    private String assumeClaimsUrl;

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
    public ApiResponse assume(@RequestParam() Map<String, String> params) throws Exception {

        ApiResponse apiResponse;

        String netid = params.get("netid");

        String response = HttpUtility.makeHttpRequest(String.format("%s?netid=%s", assumeClaimsUrl, netid), "GET");

        Map<String, String> info = objectMapper.readValue(response, Map.class);
        info.put("netid", netid);

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

            apiResponse = new ApiResponse(ApiStatus.SUCCESS, "Assume token request successful.",
                    tokenService.craftToken(claims));

        } else {
            apiResponse = new ApiResponse(ApiStatus.INVALID, "netid not found");
        }

        return apiResponse;
    }

}
