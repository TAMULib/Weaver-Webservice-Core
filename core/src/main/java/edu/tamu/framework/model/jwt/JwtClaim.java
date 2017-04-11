/* 
 * JwtClaim.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.jwt;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON Web Token Claims.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 * 
 */
public class JwtClaim {

    private Map<String, String> claim;

    /**
     * Constructor.
     *
     * @param claim
     *            Map<String, String>
     *
     * @exception JsonProcessingException
     * 
     */
    public JwtClaim(Map<String, String> claim) throws JsonProcessingException {
        this.claim = claim;
    }

    /**
     * Retrieve contents of claims as a Map.
     *
     * @return Map<String, String>
     *
     */
    public Map<String, String> getContentAsMap() {
        return claim;
    }

    /**
     * Set contents of claim.
     *
     * @param claim
     *            Map<String, String>
     *
     */
    public void setContent(Map<String, String> claim) {
        this.claim = claim;
    }

    /**
     * Add claims.
     *
     * @param key
     *            String
     * @param value
     *            String
     *
     */
    public void putClaim(String key, String value) {
        this.claim.put(key, value);
    }

    /**
     * Retrieve claim as a JSON.
     *
     * @return String
     *
     * @exception JsonProcessingException
     * 
     */
    public String getClaimAsJSON() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(claim);
    }

}
