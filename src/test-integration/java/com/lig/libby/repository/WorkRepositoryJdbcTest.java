package com.lig.libby.repository;

import com.lig.libby.domain.Work;
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
@Import({WorkRepository.class, WorkRepositoryJdbc.class})
@ActiveProfiles({"shellDisabled", "springJdbc", "WorkRepositoryJdbcTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WorkRepositoryJdbcTest {

    private final WorkRepositoryTest workRepositoryTest;

    @Autowired
    public WorkRepositoryJdbcTest(@NonNull WorkRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Work> entityFactoryWork, @NonNull EntityManager em2) {
        workRepositoryTest = new WorkRepositoryTest(repository, em, entityFactoryWork, em2);
    }

    @Test
    public void saveAndQueryTest() {
        workRepositoryTest.saveAndQueryTest();
    }

    @Test
    public void updateAndQueryTest() {
        workRepositoryTest.updateAndQueryTest();
    }

    @Test
    public void findWithPredicateTest() {
        workRepositoryTest.findWithPredicateTest();
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(workRepositoryTest.repository instanceof WorkRepositoryJdbc
                || AopUtils.getTargetClass(workRepositoryTest.repository).equals(WorkRepositoryJdbc.class)
        ).isTrue();
    }

    @Profile("WorkRepositoryJdbcTest")
    @TestConfiguration
    static class IntegrationTestConfiguration extends WorkRepositoryTest.IntegrationTestConfiguration {
    }


}