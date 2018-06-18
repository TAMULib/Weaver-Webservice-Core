package edu.tamu.weaver.data.model;

public interface WeaverEntity extends Comparable<WeaverEntity> {

    public Long getId();

    public void setId(Long id);

    public boolean equals(Object obj);

    public int hashCode();

    public int compareTo(WeaverEntity o);

}
