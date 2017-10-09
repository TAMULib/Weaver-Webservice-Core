/* 
 * AbstractCoreUser.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Abstract Core User Implementation.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Entity
@Table(name = "core_users")
public abstract class AbstractCoreUser extends BaseEntity implements CoreUser, UserDetails {

    private static final long serialVersionUID = -4974106399870286015L;

    @Column(name = "uin", nullable = false, unique = true)
    private String uin;

    public AbstractCoreUser() {
    }

    public AbstractCoreUser(String uin) {
        this();
        this.uin = uin;
    }

    public Long getId() {
        return id;
    }

    @Override
    public void setUin(String uin) {
        this.uin = uin;
    }

    @Override
    public String getUin() {
        return uin;
    }

    @Override
    public abstract void setRole(IRole role);

    @Override
    public abstract IRole getRole();

}
