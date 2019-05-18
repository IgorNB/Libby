package com.lig.libby.repository;

import com.lig.libby.domain.Comment;
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
@Import({CommentRepository.class, CommentRepositoryJdbc.class})
@ActiveProfiles({"shellDisabled", "springJdbc", "CommentRepositoryJdbcTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryJdbcTest {

    private final CommentRepositoryTest commentRepositoryTest;

    @Autowired
    public CommentRepositoryJdbcTest(@NonNull CommentRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Comment> entityFactoryComment, @NonNull EntityManager em2) {
        commentRepositoryTest = new CommentRepositoryTest(repository, em, entityFactoryComment, em2);
    }

    @Test
    public void saveAndQueryTest() {
        commentRepositoryTest.saveAndQueryTest();
    }

    @Test
    public void updateAndQueryTest() {
        commentRepositoryTest.updateAndQueryTest();
    }

    @Test
    public void findWithPredicateTest() {
        commentRepositoryTest.findWithPredicateTest();
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(commentRepositoryTest.repository instanceof CommentRepositoryJdbc
                || AopUtils.getTargetClass(commentRepositoryTest.repository).equals(CommentRepositoryJdbc.class)
        ).isTrue();
    }

    @Profile("CommentRepositoryJdbcTest")
    @TestConfiguration
    static class IntegrationTestConfiguration extends CommentRepositoryTest.IntegrationTestConfiguration {
    }
}