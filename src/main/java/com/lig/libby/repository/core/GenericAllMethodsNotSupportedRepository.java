package com.lig.libby.repository.core;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public class GenericAllMethodsNotSupportedRepository<E, Q extends EntityPathBase<E>, I> implements GenericUiApiRepository<E, Q, I> {

    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";


    @Override
    public Optional<E> findOne(Predicate predicate) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Iterable<E> findAll(Predicate predicate) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Iterable<E> findAll(Predicate predicate, Sort sort) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Iterable<E> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Iterable<E> findAll(OrderSpecifier<?>... orders) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Page<E> findAll(Predicate predicate, Pageable pageable) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public long count(Predicate predicate) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean exists(Predicate predicate) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> S save(S entity) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public List<E> findAll() {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public List<E> findAll(Sort sort) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public List<E> findAllById(Iterable<I> is) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public long count() {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void deleteById(I i) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void delete(E entity) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void deleteAll(Iterable<? extends E> entities) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void deleteAll() {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> List<S> saveAll(Iterable<S> entities) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public Optional<E> findById(I i) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean existsById(I i) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void flush() {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> S saveAndFlush(S entity) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void deleteInBatch(Iterable<E> entities) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void deleteAllInBatch() {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public E getOne(I i) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> Optional<S> findOne(Example<S> example) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> List<S> findAll(Example<S> example) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> List<S> findAll(Example<S> example, Sort sort) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> long count(Example<S> example) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public <S extends E> boolean exists(Example<S> example) {
        throw new RuntimeException(OPERATION_NOT_SUPPORTED);
    }
}
