package com.lig.libby.repository.core;

import com.lig.libby.domain.core.PersistentObject;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


class GenericAllMethodsNotSupportedRepositoryTest {

    private static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    private final GenericAllMethodsNotSupportedRepository dao = new GenericAllMethodsNotSupportedRepository();

    @Test
    void methodsNotSupported() throws ClassNotFoundException {
        Predicate predicate = Mockito.mock(Predicate.class);
        Sort sort = Mockito.mock(Sort.class);
        OrderSpecifier orderSpecifier = Mockito.mock(OrderSpecifier.class);
        Pageable pageable = Mockito.mock(Pageable.class);PersistentObject entity = Mockito.mock(PersistentObject.class);
        Iterable iterable = Mockito.mock(Iterable.class);
        String i = "";
        Iterable iterableEntities = Arrays.asList(entity,entity);
        Example example = Mockito.mock(Example.class);
        assertAll(
                () -> assertThatThrownBy(() -> dao.findOne(predicate)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll(predicate)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll(predicate,  sort)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll(predicate, orderSpecifier)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll(orderSpecifier)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll(predicate, pageable)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () ->  assertThatThrownBy(() -> dao.count(predicate)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.exists(predicate)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.save(entity)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll()).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll(sort)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAllById(iterable)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findAll(pageable)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.count()).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.deleteById(i)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.delete(entity)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.deleteAll(iterableEntities)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.deleteAll()).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.saveAll(iterableEntities)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () -> assertThatThrownBy(() -> dao.findById(i)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () ->assertThatThrownBy(() -> dao.existsById(i)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.flush()).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.saveAndFlush(entity)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () ->assertThatThrownBy(() -> dao.deleteInBatch(iterableEntities)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.deleteAllInBatch()).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.getOne(i)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () ->assertThatThrownBy(() -> dao.findOne(example)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.findAll(example)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.findAll(example, sort)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                () ->assertThatThrownBy(() -> dao.findAll(example, pageable)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.count(example)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED),
                ()->assertThatThrownBy(() -> dao.exists(example)).hasSameClassAs(new RuntimeException()).hasMessage(OPERATION_NOT_SUPPORTED)
        );
    }
}