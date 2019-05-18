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
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public abstract class GenericQueryDslRepositoryJpa<T extends PersistentObject, Q extends EntityPathBase<T>, I extends Serializable> extends GenericRepositoryJpa<T, Q, I>
        implements QuerydslPredicateExecutor<T> {

    private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;

    private final EntityPath<T> path;
    private final PathBuilder<T> builder;
    private final Querydsl querydsl;


    public GenericQueryDslRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
        this.path = DEFAULT_ENTITY_PATH_RESOLVER.createPath(getDomainClass());
        this.builder = new PathBuilder<>(path.getType(), path.getMetadata());
        this.querydsl = new Querydsl(entityManager, builder);

    }

    @Override
    public abstract Class<T> getDomainClass();

    @Override
    public Optional<T> findOne(Predicate predicate) {

        try {
            return Optional.ofNullable(createQuery(predicate).select(path).fetchOne());
        } catch (NonUniqueResultException ex) {
            throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
        }
    }

    @Override
    public List<T> findAll(Predicate predicate) {
        return createQuery(predicate).select(path).fetch();
    }

    @Override
    public List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
        return executeSorted(createQuery(predicate).select(path), orders);
    }

    @Override
    public List<T> findAll(Predicate predicate, Sort sort) {

        Assert.notNull(sort, "Sort must not be null!");

        return executeSorted(createQuery(predicate).select(path), sort);
    }

    @Override
    public List<T> findAll(OrderSpecifier<?>... orders) {

        Assert.notNull(orders, "Order specifiers must not be null!");

        return executeSorted(createQuery(new Predicate[0]).select(path), orders);
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {

        Assert.notNull(pageable, "Pageable must not be null!");

        final JPQLQuery<?> countQuery = createCountQuery(predicate);
        JPQLQuery<T> query = querydsl.applyPagination(pageable, createQuery(predicate).select(path));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchCount);
    }

    @Override
    public long count(Predicate predicate) {
        return createQuery(predicate).fetchCount();
    }

    @Override
    public boolean exists(Predicate predicate) {
        return createQuery(predicate).fetchCount() > 0;
    }

    @SuppressWarnings("squid:S1452")
    //this is how external library QueryDSL designed - we skip rule "Generic wildcard types should not be used in return parameters" here
    protected JPQLQuery<?> createQuery(Predicate... predicate) {
        return doCreateQuery(predicate);
    }

    @SuppressWarnings("squid:S1452")
    //this is how external library QueryDSL designed - we skip rule "Generic wildcard types should not be used in return parameters" here
    protected JPQLQuery<?> createCountQuery(@Nullable Predicate... predicate) {
        return doCreateQuery(predicate);
    }

    private AbstractJPAQuery<?, ?> doCreateQuery(@Nullable Predicate... predicate) {

        AbstractJPAQuery<?, ?> query = querydsl.createQuery(path);

        if (predicate != null) {
            query = query.where(predicate);
        }

        return query;
    }

    private List<T> executeSorted(JPQLQuery<T> query, OrderSpecifier<?>... orders) {
        return executeSorted(query, new QSort(orders));
    }

    private List<T> executeSorted(JPQLQuery<T> query, Sort sort) {
        return querydsl.applySorting(sort, query).fetch();
    }
}
