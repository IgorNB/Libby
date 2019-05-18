/*
 * Copyright 2008-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lig.libby.repository.core.jpa;

import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.core.GenericAllMethodsNotSupportedRepository;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

@Repository
@Transactional(readOnly = true)
public abstract class GenericRepositoryJpa<T extends PersistentObject, Q extends EntityPathBase<T>, I> extends GenericAllMethodsNotSupportedRepository<T, Q, I> implements JpaRepository<T, I> {

    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";
    private final EntityManager em;


    @Autowired
    public GenericRepositoryJpa(EntityManager entityManager) {
        Assert.notNull(entityManager, "EntityManager must not be null!");
        this.em = entityManager;
    }

    public abstract Class<T> getDomainClass();

    @Transactional
    @Override
    public void deleteById(I id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        delete(findById(id).orElseThrow(() -> new EmptyResultDataAccessException(
                String.format("No entity with id %s exists!", id), 1)));
    }


    @Transactional
    @Override
    public void delete(T entity) {
        Assert.notNull(entity, "The entity must not be null!");
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public Optional<T> findById(I id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        Class<T> domainType = getDomainClass();
        return Optional.ofNullable(em.find(domainType, id));
    }

    @Override
    public List<T> findAll() {
        Sort sort = Sort.unsorted();
        Class<T> domainClass = getDomainClass();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);
        Root<T> root = query.from(domainClass);
        query.select(root);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }
        return em.createQuery(query).getResultList();
    }


    @Transactional
    @Override
    public <S extends T> S save(S entity) {

        if (entity.getVersion() == null) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    @Transactional
    @Override
    public <S extends T> S saveAndFlush(S entity) {
        S result = save(entity);
        flush();
        return result;
    }

    @Transactional
    @Override
    public void flush() {
        em.flush();
    }

    @Transactional
    @Override
    public void deleteAll() {
        findAll().forEach(this::delete);
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }
}
