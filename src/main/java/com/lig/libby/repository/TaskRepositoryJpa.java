package com.lig.libby.repository;

import com.lig.libby.domain.QTask;
import com.lig.libby.domain.Task;
import com.lig.libby.repository.core.jpa.GenericQueryDslRepositoryJpa;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@ThreadSafe
@Profile("springJpa")
@Repository
public class TaskRepositoryJpa extends GenericQueryDslRepositoryJpa<Task, QTask, String> implements TaskRepository {

    public TaskRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Class<Task> getDomainClass() {
        return Task.class;
    }
}
