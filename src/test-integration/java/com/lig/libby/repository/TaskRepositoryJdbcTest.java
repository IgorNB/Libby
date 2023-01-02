package com.lig.libby.repository;

import com.lig.libby.domain.Task;
import com.lig.libby.repository.common.DataJpaAuditConfig;
import com.lig.libby.repository.common.EntityFactory;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = {DataJpaAuditConfig.class}))
@Import({TaskRepository.class, TaskRepositoryJdbc.class})
@ActiveProfiles({"shellDisabled", "springJdbc", "TaskRepositoryJdbcTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryJdbcTest {

    private final TaskRepositoryTest taskRepositoryTest;

    @Autowired
    public TaskRepositoryJdbcTest(@NonNull TaskRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Task> entityFactoryTask, @NonNull EntityManager em2) {
        taskRepositoryTest = new TaskRepositoryTest(repository, em, entityFactoryTask, em2);
    }

    @Test
    public void saveAndQueryTest() {
        taskRepositoryTest.saveAndQueryTest();
    }

    @Test
    public void updateAndQueryTest() {
        taskRepositoryTest.updateAndQueryTest();
    }

    @Test
    public void findWithPredicateTest() {
        taskRepositoryTest.findWithPredicateTest();
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(taskRepositoryTest.repository instanceof TaskRepositoryJdbc
                || AopUtils.getTargetClass(taskRepositoryTest.repository).equals(TaskRepositoryJdbc.class)
        ).isTrue();
    }

    @Profile("TaskRepositoryJdbcTest")
    @TestConfiguration
    static class IntegrationTestConfiguration extends TaskRepositoryTest.IntegrationTestConfiguration {
    }
}