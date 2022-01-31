package edu.tamu.weaver.auth.model;

import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;

/**
 * Credentials object.
 *
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class Credentials {

    private String lastName;
    private String firstName;
    private String netid;
    private String uin;
    private String exp;
    private String email;
    private String role;
    private String affiliation;
    private Map<String, String> allCredentials;

    public Credentials() {
        allCredentials = new HashMap<String, String>();
    }

    /**
     * Constructor
     * 
     * @param claims
     *            Claims
     * 
     */
    public Credentials(Claims claims) {
        this();
        this.lastName = String.valueOf(claims.get("lastName"));
        this.firstName = String.valueOf(claims.get("firstName"));
        this.netid = String.valueOf(claims.get("netid"));
        this.uin = String.valueOf(claims.get("uin"));
        this.exp = String.valueOf(claims.get("exp"));
        this.email = String.valueOf(claims.get("email"));
        this.role = String.valueOf(claims.get("role"));
        this.affiliation = String.valueOf(claims.getOrDefault("affiliation", ""));
        claims.entrySet().forEach(entry -> {
            this.allCredentials.put(entry.getKey(), String.valueOf(entry.getValue()));
        });
    }

    /**
     * Constructor
     * 
     * @param claims
     *            Map<String, Object>
     * 
     */
    public Credentials(Map<String, Object> claims) {
        this();
        this.lastName = String.valueOf(claims.get("lastName"));
        this.firstName = String.valueOf(claims.get("firstName"));
        this.netid = String.valueOf(claims.get("netid"));
        this.uin = String.valueOf(claims.get("uin"));
        this.exp = String.valueOf(claims.get("exp"));
        this.email = String.valueOf(claims.get("email"));
        this.role = String.valueOf(claims.get("role"));
        this.affiliation = String.valueOf(claims.getOrDefault("affiliation", ""));
        claims.entrySet().forEach(entry -> {
            this.allCredentials.put(entry.getKey(), String.valueOf(entry.getValue()));
        });
    }

    /**
     * Gets last name.
     * 
     * @return String
     * 
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets last name.
     * 
     * @param lastName
     *            String
     * 
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets first name.
     * 
     * @return String
     * 
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets first name.
     * 
     * @param firstName
     *            String
     * 
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the netid
     */
    public String getNetid() {
        return netid;
    }

    /**
     * @param netid
     *            the netid to set
     */
    public void setNetid(String netid) {
        this.netid = netid;
    }

    /**
     * Gets UIN.
     * 
     * @return String
     * 
     */
    public String getUin() {
        return this.uin;
    }

    /**
     * Sets UIN.
     * 
     * @param uin
     *            String
     * 
     */
    public void setUin(String uin) {
        this.uin = uin;
    }

    /**
     * Gets expiration.
     * 
     * @return String
     * 
     */
    public String getExp() {
        return this.exp;
    }

    /**
     * Sets expiration.
     * 
     * @param exp
     *            String
     * 
     */
    public void setExp(String exp) {
        this.exp = exp;
    }

    /**
     * Gets email.
     * 
     * @return String
     * 
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets email.
     * 
     * @param email
     *            String
     * 
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets role.
     * 
     * @return String
     * 
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Sets role.
     * 
     * @param role
     *            String
     * 
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return affiliation
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * @param affiliation
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    /**
     * @return all credentials
     */
    public Map<String, String> getAllCredentials() {
        return this.allCredentials;
    }

    /**
     * @param allCredentials
     */
    public void setAllCredentials(Map<String, String> allCredentials) {
        this.allCredentials = allCredentials;
    }

}
