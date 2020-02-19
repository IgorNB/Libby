package com.lig.libby.repository;

import com.lig.libby.domain.QWork;
import com.lig.libby.domain.Work;
import com.lig.libby.repository.core.jpa.GenericQueryDslRepositoryJpa;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@ThreadSafe
@Profile("springJpa")
@Repository
public class WorkRepositoryJpa extends GenericQueryDslRepositoryJpa<Work, QWork, String> implements WorkRepository {

    public WorkRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Class<Work> getDomainClass() {
        return Work.class;
    }
}
