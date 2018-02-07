/* 
 * BaseOrderedEntity.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Deprecated
public abstract class BaseOrderedEntity extends BaseEntity {

    @Column(nullable = true)
    protected Long position = null;

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

}
