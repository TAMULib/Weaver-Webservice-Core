/* 
 * JWTheader.java 
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
 * JSON Web Token header.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 * 
 */
public class JwtHeader {

    private Map<String, String> header;

    /**
     * Constructor.
     *
     * @param header
     *            Map<String, String>
     *
     * @exception JsonProcessingException
     * 
     */
    public JwtHeader(Map<String, String> header) throws JsonProcessingException {
        this.header = header;
        header.put("alg", "HS256");
        header.put("typ", "JWT");
    }

    /**
     * Retrieve header as map.
     *
     * @return Map<String, String>
     *
     */
    public Map<String, String> getHeaderAsMap() {
        return header;
    }

    /**
     * Set header from map.
     *
     * @param header
     *            Map<String, String>
     *
     */
    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    /**
     * Retrieve header as a JSON.
     *
     * @return String
     *
     * @exception JsonProcessingException
     * 
     */
    public String getHeaderAsJSON() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(header);
    }

}
