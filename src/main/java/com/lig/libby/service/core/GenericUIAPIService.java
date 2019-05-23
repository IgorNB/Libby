package com.lig.libby.service.core;

import com.lig.libby.domain.core.PersistentObject;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface GenericUIAPIService<E extends PersistentObject, I> {
    E findById(@NonNull I id, @NonNull UserDetails userDetails);

    @NonNull Page<E> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails);


    @NonNull E update(@NonNull E entity, @NonNull UserDetails userDetails);


    @NonNull E create(@NonNull E entity, @NonNull UserDetails userDetails);

    void deleteById(@NonNull String id);
}
