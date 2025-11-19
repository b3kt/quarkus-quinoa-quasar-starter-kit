package com.github.b3kt.application.service.pazaauto;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Generic CRUD service that delegates to a Panache repository.
 *
 * @param <T>  entity type
 * @param <ID> identifier type
 */
public abstract class AbstractCrudService<T, ID> {

    protected abstract PanacheRepositoryBase<T, ID> getRepository();

    protected abstract void setEntityId(T entity, ID id);

    public List<T> findAll() {
        return getRepository().listAll();
    }

    public T findById(ID id) {
        return getRepository().findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
    }

    @Transactional
    public T create(T entity) {
        getRepository().persist(entity);
        return entity;
    }

    @Transactional
    public T update(ID id, T entity) {
        if (!getRepository().findByIdOptional(id).isPresent()) {
            throw new EntityNotFoundException("Entity not found with id: " + id);
        }
        setEntityId(entity, id);
        return getRepository().getEntityManager().merge(entity);
    }

    @Transactional
    public void delete(ID id) {
        boolean deleted = getRepository().deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Entity not found with id: " + id);
        }
    }
}

