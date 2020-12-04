package edu.tamu.weaver.data.resolver;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;

import org.springframework.stereotype.Component;

import edu.tamu.weaver.context.SpringContext;

@Component
public class BaseEntityIdResolver implements ObjectIdResolver {

    @PersistenceContext
    private EntityManager entityManager;

    public BaseEntityIdResolver() {

    }

    public BaseEntityIdResolver(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void bindItem(final ObjectIdGenerator.IdKey id, final Object pojo) {

    }

    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey id) {
        return this.entityManager.find(id.scope, id.key);
    }

    @Override
    public boolean canUseFor(final ObjectIdResolver resolverType) {
        return getClass().isAssignableFrom(resolverType.getClass());
    }

    @Override
    public ObjectIdResolver newForDeserialization(final Object context) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        return new BaseEntityIdResolver(entityManager);
    }

}
