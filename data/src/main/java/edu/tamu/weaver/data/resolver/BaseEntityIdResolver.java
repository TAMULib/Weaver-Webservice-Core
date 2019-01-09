package edu.tamu.weaver.data.resolver;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;

public class BaseEntityIdResolver implements ObjectIdResolver {

    private EntityManager entityManager;

    public BaseEntityIdResolver() {

    }

    public BaseEntityIdResolver(final EntityManager entityManager) {
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
    public ObjectIdResolver newForDeserialization(final Object context) {
        return this;
    }

    @Override
    public boolean canUseFor(final ObjectIdResolver resolverType) {
        return false;
    }

}
