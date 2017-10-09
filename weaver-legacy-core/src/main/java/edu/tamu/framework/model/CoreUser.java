/* 
 * CoreUser.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

/**
 * Core user interface. lol
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public interface CoreUser {

    /**
     * Sets UIN.
     * 
     * @param uin
     *            String
     * 
     */
    public void setUin(String uin);

    /**
     * Gets UIN.
     * 
     * @return String
     * 
     */
    public String getUin();

    /**
     * Sets role.
     * 
     * @param role
     *            String
     * 
     */
    public void setRole(IRole role);

    /**
     * Gets role.
     * 
     * @return String
     * 
     */
    public IRole getRole();

}
