package com.lig.libby.service;

import com.lig.libby.domain.Lang;
import com.lig.libby.repository.LangRepository;
import com.lig.libby.service.core.GenericFallbackUIAPIService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@ThreadSafe
@Service
public class LangServiceImpl implements LangService, GenericFallbackUIAPIService<Lang, String> {
    private static final String CMD_KEY_PRX = "LangService_";
    private final LangRepository langRepository;

    @Autowired
    public LangServiceImpl(@NonNull LangRepository langRepository) {
        this.langRepository = langRepository;
    }

    @HystrixCommand(fallbackMethod = FIND_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + FIND_BY_ID_FALLBACK)
    @Override
    public Lang findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return langRepository.findById(id).orElse(null);
    }

    @HystrixCommand(fallbackMethod = FIND_ALL_FALLBACK, commandKey = CMD_KEY_PRX + FIND_ALL_FALLBACK)
    @Override
    public @NonNull Page<Lang> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        return langRepository.findAll(predicate, pageable);
    }

    @HystrixCommand(fallbackMethod = UPDATE_FALLBACK, commandKey = CMD_KEY_PRX + UPDATE_FALLBACK)
    @NonNull
    @Override
    public Lang update(@NonNull Lang entity, @NonNull UserDetails userDetails) {
        return langRepository.save(entity);
    }

    @HystrixCommand(fallbackMethod = CREATE_FALLBACK, commandKey = CMD_KEY_PRX + CREATE_FALLBACK)
    @NonNull
    @Override
    public Lang create(@NonNull Lang entity, @NonNull UserDetails userDetails) {
        return langRepository.save(entity);
    }

    @HystrixCommand(fallbackMethod = DELETE_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + DELETE_BY_ID_FALLBACK)
    @Override
    public void deleteById(@NonNull String id) {
        langRepository.deleteById(id);
    }

    @Override
    public Lang findByIdFallback(@NonNull String id, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public @NonNull Page<Lang> findAllFallback(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public Lang updateFallback(@NonNull Lang entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public Lang createFallback(@NonNull Lang entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public void deleteByIdFallback(@NonNull String id, Throwable cause) {
        throw apiFallbackException(cause);
    }
}
