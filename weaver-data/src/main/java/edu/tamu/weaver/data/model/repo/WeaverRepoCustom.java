package edu.tamu.weaver.data.model.repo;

public interface WeaverRepoCustom<M> {

	public M create(M model);

	public M read(Long id);

	public M update(M model);

	public void delete(M model);

}
