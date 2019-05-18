package com.lig.libby.repository;

import com.lig.libby.domain.Authority;
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
@Import({AuthorityRepository.class, AuthorityRepositoryJdbc.class})
@ActiveProfiles({"shellDisabled", "springJdbc", "AuthorityRepositoryJdbcTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorityRepositoryJdbcTest {

    private final AuthorityRepositoryTest authorityRepositoryTest;

    @Autowired
    public AuthorityRepositoryJdbcTest(@NonNull AuthorityRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Authority> entityFactoryAuthority, @NonNull EntityManager em2) {
        authorityRepositoryTest = new AuthorityRepositoryTest(repository, em, entityFactoryAuthority, em2);
    }

    @Test
    public void saveAndQueryTest() {
        authorityRepositoryTest.saveAndQueryTest();
    }

    @Test
    public void updateAndQueryTest() {
        authorityRepositoryTest.updateAndQueryTest();
    }

    @Test
    public void findWithPredicateTest() {
        authorityRepositoryTest.findWithPredicateTest();
    }

    @Test
    public void getAuthorityByNameTest() {
        authorityRepositoryTest.getAuthorityByName();
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(authorityRepositoryTest.repository instanceof AuthorityRepositoryJdbc
                || AopUtils.getTargetClass(authorityRepositoryTest.repository).equals(AuthorityRepositoryJdbc.class)
        ).isTrue();
    }

    @Profile("AuthorityRepositoryJdbcTest")
    @TestConfiguration
    static class IntegrationTestConfiguration extends AuthorityRepositoryTest.IntegrationTestConfiguration {
    }
}