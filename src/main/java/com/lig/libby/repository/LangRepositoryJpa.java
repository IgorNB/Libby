package com.lig.libby.repository;

import com.lig.libby.domain.Lang;
import com.lig.libby.domain.QLang;
import com.lig.libby.repository.core.jpa.GenericQueryDslRepositoryJpa;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@ThreadSafe
@Profile("springJpa")
@Repository
public class LangRepositoryJpa extends GenericQueryDslRepositoryJpa<Lang, QLang, String> implements LangRepository {

    public LangRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Class<Lang> getDomainClass() {
        return Lang.class;
    }
}
