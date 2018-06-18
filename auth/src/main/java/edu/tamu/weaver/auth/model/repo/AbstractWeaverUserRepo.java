package edu.tamu.weaver.auth.model.repo;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import edu.tamu.weaver.data.model.repo.WeaverRepo;
import edu.tamu.weaver.user.model.AbstractWeaverUser;

@NoRepositoryBean
public interface AbstractWeaverUserRepo<U extends AbstractWeaverUser> extends WeaverRepo<U> {

    public Optional<U> findByUsername(String username);

}