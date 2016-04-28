/* 
 * CoreRole.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.enums;

import edu.tamu.framework.model.IRole;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public enum CoreRole implements IRole {
	
	ROLE_NONE(0), 
	ROLE_ANONYMOUS(1), 
	ROLE_USER(2),
	ROLE_MANAGER(3),
	ROLE_ADMIN(4);
    
    private int value;

    CoreRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
    
}
