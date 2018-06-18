package edu.tamu.weaver.data.resolver;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;

public abstract class EntityByPropertyResolver implements ObjectIdResolver {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityByPropertyResolver() {

    }

    @Override
    public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {

    }

    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey idKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> e = query.from(idKey.scope);
        query.select(e).distinct(true);
        query.where(cb.equal(e.get(getPropertyName()), idKey.key));
        Object entity = entityManager.createQuery(query).getSingleResult();
        return entity;
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return getClass().isAssignableFrom(resolverType.getClass());
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object c) {
        return this;
    }

    protected abstract String getPropertyName();

}
