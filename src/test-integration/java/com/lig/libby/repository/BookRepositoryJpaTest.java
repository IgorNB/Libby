package com.lig.libby.repository;

import com.lig.libby.domain.Book;
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
@Import({BookRepository.class, BookRepositoryJpa.class})
@ActiveProfiles({"shellDisabled", "springJpa", "BookRepositoryJpaTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryJpaTest {

    private final BookRepositoryTest bookRepositoryTest;

    @Autowired
    public BookRepositoryJpaTest(@NonNull BookRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Book> entityFactoryBook, @NonNull EntityManager em2) {
        bookRepositoryTest = new BookRepositoryTest(repository, em, entityFactoryBook, em2);
    }

    @Test
    public void saveAndQueryTest() {
        bookRepositoryTest.saveAndQueryTest();
    }

    @Test
    public void updateAndQueryTest() {
        bookRepositoryTest.updateAndQueryTest();
    }

    @Test
    public void findWithPredicateTest() {
        bookRepositoryTest.findWithPredicateTest();
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(bookRepositoryTest.repository instanceof BookRepositoryJpa
                || AopUtils.getTargetClass(bookRepositoryTest.repository).equals(BookRepositoryJpa.class)
        ).isTrue();
    }

    @Profile("BookRepositoryJpaTest")
    @TestConfiguration
    static class IntegrationTestConfiguration extends BookRepositoryTest.IntegrationTestConfiguration {
    }
}