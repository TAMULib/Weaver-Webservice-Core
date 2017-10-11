package edu.tamu.weaver.data.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import edu.tamu.weaver.data.model.WeaverEntity;

@NoRepositoryBean
public interface WeaverRepo<M extends WeaverEntity> extends JpaRepository<M, Long>, WeaverRepoCustom<M> {

    @Override
    public void delete(M model);
    
    public List<M> findAllByOrderByPositionAsc();

}
