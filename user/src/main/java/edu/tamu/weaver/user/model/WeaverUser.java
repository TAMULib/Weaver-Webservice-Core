package edu.tamu.weaver.user.model;

/**
 * Weaver User interface.
 *
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public interface WeaverUser {

    /**
     * Sets username.
     *
     * @param username
     *            String
     *
     */
    public void setUsername(String username);

    /**
     * Gets username.
     *
     * @return String
     *
     */
    public String getUsername();

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
