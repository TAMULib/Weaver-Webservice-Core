/*
 * BaseOrderedEntity.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
package edu.tamu.weaver.data.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class OrderedBaseEntity extends BaseEntity implements WeaverOrderedEntity {

    @Column(nullable = true)
    protected Long position = null;

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

}
