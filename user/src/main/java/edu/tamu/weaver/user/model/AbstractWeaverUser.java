package edu.tamu.weaver.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

/**
 * Abstract Weaver User Implementation.
 *
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 *
 */
@Entity
@Table(name = "weaver_users")
public abstract class AbstractWeaverUser extends ValidatingBaseEntity implements WeaverUser {

    @Column(nullable = false, unique = true)
    private String username;

    public AbstractWeaverUser() {

    }

    public AbstractWeaverUser(String username) {
        this();
        this.username = username;
    }

    public AbstractWeaverUser(AbstractWeaverUser user) {
        this(user.getUsername());
        this.id = user.getId();
        setRole(user.getRole());
    }

    public Long getId() {
        return id;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public abstract void setRole(IRole role);

    @Override
    public abstract IRole getRole();

}
