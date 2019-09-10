package com.lig.libby.service.core;

import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.service.exception.ServiceFallbackException;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("squid:S1214")//here we skip Sonar rule "Constants should not be defined in interfaces", because here we does not use interface to store constants, but we store name of interface methods in constants to use them in annotations
public interface GenericFallbackUIAPIService<E extends PersistentObject, I> {
    public static String FIND_BY_ID_FALLBACK = "findByIdFallback";
    public static String FIND_ALL_FALLBACK = "findAllFallback";
    public static String UPDATE_FALLBACK = "updateFallback";
    public static String CREATE_FALLBACK = "createFallback";
    public static String DELETE_BY_ID_FALLBACK = "deleteByIdFallback";

    E findByIdFallback(@NonNull I id, @NonNull UserDetails userDetails, Throwable cause);

    @NonNull Page<E> findAllFallback(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails, Throwable cause);

    @Transactional
    @NonNull E updateFallback(@NonNull E entity, @NonNull UserDetails userDetails, Throwable cause);

    @Transactional
    @NonNull E createFallback(@NonNull E entity, @NonNull UserDetails userDetails, Throwable cause);

    void deleteByIdFallback(@NonNull I id, Throwable cause);

    default RuntimeException apiFallbackException(Throwable cause) {
        return new ServiceFallbackException("api", cause);
    }
}
