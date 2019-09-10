package com.lig.libby.service;

import com.lig.libby.domain.Lang;
import com.lig.libby.repository.LangRepository;
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
public class LangServiceImpl implements LangService {
    private final LangRepository langRepository;

    @Autowired
    public LangServiceImpl(@NonNull LangRepository langRepository) {
        this.langRepository = langRepository;
    }

    @Override
    public Lang findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return langRepository.findById(id).orElse(null);
    }

    @Override
    public @NonNull Page<Lang> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        return langRepository.findAll(predicate, pageable);
    }

    @NonNull
    @Override
    public Lang update(@NonNull Lang entity, @NonNull UserDetails userDetails) {
        return langRepository.save(entity);
    }

    @NonNull
    @Override
    public Lang create(@NonNull Lang entity, @NonNull UserDetails userDetails) {
        return langRepository.save(entity);
    }

    @Override
    public void deleteById(@NonNull String id) {
        langRepository.deleteById(id);
    }
}
