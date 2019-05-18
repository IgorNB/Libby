package com.lig.libby.controller.core.uiadapter;

import com.lig.libby.domain.core.PersistentObject;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;

public interface GenricUIApiController<D extends PersistentObject, I> {


    @GetMapping
        //Attention! Overrige root with your Entity class. E.g. your controller should look like this: Page<EntityDTO> findAll(@QuerydslPredicate(root = Entity.class) Predicate predicate, Pageable pageable)
    Page<D> findAll(@QuerydslPredicate Predicate predicate, Pageable pageable, Authentication authentication);

    @GetMapping(value = "/{id}")
    D findOne(@PathVariable("id") I id, Authentication authentication);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    D create(@RequestBody D resource, Authentication authentication) throws OperationNotSupportedException;

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    D update(@RequestBody D resource, Authentication authentication) throws OperationNotSupportedException;

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    void delete(@PathVariable("id") I id, Authentication authentication) throws OperationNotSupportedException;
}
