package edu.tamu.weaver.data.model.repo;

import java.util.List;

public interface WeaverRepoCustom<M> {

	public M create(M model);

	public M read(Long id);

	public M update(M model);

	public void delete(M model);

	public void broadcast(List<M> repo);
	
	public void broadcast(M model);

	public void broadcast(Long id);

}
