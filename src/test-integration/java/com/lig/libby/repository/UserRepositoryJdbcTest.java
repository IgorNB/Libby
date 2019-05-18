package com.lig.libby.repository;

import com.lig.libby.domain.User;
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
@Import({UserRepository.class, UserRepositoryJdbc.class})
@ActiveProfiles({"shellDisabled", "springJdbc", "UserRepositoryJdbcTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryJdbcTest {

    private final UserRepositoryTest userRepositoryTest;

    @Autowired
    public UserRepositoryJdbcTest(@NonNull UserRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<User> entityFactoryUser, @NonNull EntityManager em2) {
        userRepositoryTest = new UserRepositoryTest(repository, em, entityFactoryUser, em2);
    }

    @Test
    public void saveAndQueryTest() {
        userRepositoryTest.saveAndQueryTest();
    }

    @Test
    public void updateAndQueryTest() {
        userRepositoryTest.updateAndQueryTest();
    }

    @Test
    public void findWithPredicateTest() {
        userRepositoryTest.findWithPredicateTest();
    }

    @Test
    void findByEmail() {
        userRepositoryTest.findByEmail();
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(userRepositoryTest.repository instanceof UserRepositoryJdbc
                || AopUtils.getTargetClass(userRepositoryTest.repository).equals(UserRepositoryJdbc.class)
        ).isTrue();
    }

    @Profile("UserRepositoryJdbcTest")
    @TestConfiguration
    static class IntegrationTestConfiguration extends UserRepositoryTest.IntegrationTestConfiguration {
    }


}