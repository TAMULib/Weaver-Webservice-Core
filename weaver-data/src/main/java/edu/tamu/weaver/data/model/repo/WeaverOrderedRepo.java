package edu.tamu.weaver.data.model.repo;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import edu.tamu.weaver.data.model.WeaverEntity;

@NoRepositoryBean
public interface WeaverOrderedRepo<M extends WeaverEntity> extends WeaverRepo<M>, WeaverOrderedRepoCustom<M> {

    @Override
    public void delete(M model);

    public List<M> findAllByOrderByPositionAsc();

}
