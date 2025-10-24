package edu.tamu.weaver.data.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.response.ApiView;

@MappedSuperclass
public abstract class BaseEntity implements WeaverEntity {

    @JsonView(ApiView.Partial.class)
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
