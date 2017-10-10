package edu.tamu.weaver.data.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity implements WeaverEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    protected Long id;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        // if we're the same entity type
        if (obj != null && obj.getClass().equals(this.getClass())) {
            // and we have the same Id
            Long objId = ((BaseEntity) obj).getId();
            if (objId != null) {
                return objId.equals(this.getId());
            } else {
                return objId == this.getId();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public int compareTo(WeaverEntity o) {
        return this.getId().compareTo(o.getId());
    }

}
