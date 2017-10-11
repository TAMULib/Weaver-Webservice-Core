package edu.tamu.weaver.data.model.repo;

public interface WeaverOrderedRepoCustom<M> extends WeaverRepoCustom<M> {

    public void reorder(Long src, Long dest);

    public void sort(String column);

    public void remove(M model);

    public Class<?> getModelClass();

}
