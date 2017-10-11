package edu.tamu.weaver.data.model.repo;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import edu.tamu.weaver.data.model.WeaverOrderedEntity;

@NoRepositoryBean
public interface WeaverOrderedRepo<M extends WeaverOrderedEntity> extends WeaverRepo<M>, WeaverOrderedRepoCustom<M> {

    public List<M> findAllByOrderByPositionAsc();

    public M findByPosition(Long position);

}
