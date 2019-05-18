package com.lig.libby.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lig.libby.domain.*;
import com.lig.libby.repository.common.DataJpaAuditConfig;
import com.lig.libby.repository.common.EntityFactory;
import com.querydsl.core.BooleanBuilder;
import lombok.NonNull;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = {DataJpaAuditConfig.class}))
@ActiveProfiles({"shellDisabled", "springDataJpa", "BookRepositoryTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final BookRepository repository;
    private final TestEntityManager em;
    private final EntityManager entityManager;
    private final EntityFactory<Book> entityFactoryBook;

    @Autowired
    public BookRepositoryTest(@NonNull BookRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Book> entityFactoryBook, @NonNull EntityManager entityManager) {
        this.repository = repository;
        this.em = em;
        this.entityFactoryBook = entityFactoryBook;
        this.entityManager = entityManager;
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(repository instanceof QuerydslJpaRepository
                || AopUtils.getTargetClass(repository).equals(QuerydslJpaRepository.class)
        ).isTrue();
    }

    @Test
    @Transactional
    public void saveAndQueryTest() {
        final Book entity = entityFactoryBook.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Book deepEntityCopy = (Book) SerializationUtils.clone(entity);

        final Book entitySaved = repository.saveAndFlush(entity);
        final Book entityQueried = repository.findById(id).orElse(null);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        deepEntityCopy.setVersion(0);

        deepEntityCopy.setCreatedBy(entitySaved.getCreatedBy());
        deepEntityCopy.setCreatedDate(entitySaved.getCreatedDate());

        deepEntityCopy.setUpdatedDate(entitySaved.getUpdatedDate());
        deepEntityCopy.setLastUpdBy(entitySaved.getLastUpdBy());
        assertAll(
                () -> assertThat(deepEntityCopy).isEqualToComparingFieldByFieldRecursively(entitySaved),
                () -> assertThat(deepEntityCopy).isEqualToComparingFieldByFieldRecursively(entityQueried),
                () -> assertThat(gson.toJson(deepEntityCopy).toString()).isEqualTo(gson.toJson(entitySaved).toString()),
                () -> assertThat(gson.toJson(deepEntityCopy).toString()).isEqualTo(gson.toJson(entityQueried).toString())
        );
    }

    @Test
    @Transactional
    public void updateAndQueryTest() {
        final Book entitySaved = repository.saveAndFlush(entityFactoryBook.getNewEntityInstance());
        final Book deepEntitySavedCopy = (Book) SerializationUtils.clone(entitySaved);
        entitySaved.setTitle("test-update-title");

        final Book entityUpdated = repository.saveAndFlush(entitySaved);

        deepEntitySavedCopy.setTitle(entitySaved.getTitle());

        deepEntitySavedCopy.setVersion(deepEntitySavedCopy.getVersion() + 1);

        deepEntitySavedCopy.setCreatedBy(deepEntitySavedCopy.getCreatedBy());
        deepEntitySavedCopy.setCreatedDate(deepEntitySavedCopy.getCreatedDate());

        deepEntitySavedCopy.setUpdatedDate(entityUpdated.getUpdatedDate());
        deepEntitySavedCopy.setLastUpdBy(entityUpdated.getLastUpdBy());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        assertAll(
                () -> assertThat(deepEntitySavedCopy).isEqualToComparingFieldByField(entityUpdated),
                () -> assertThat(gson.toJson(deepEntitySavedCopy).toString()).isEqualTo(gson.toJson(entityUpdated).toString())
        );
    }

    @Test
    @Transactional
    public void findWithPredicateTest() {
        final Book entitySaved1 = repository.saveAndFlush(entityFactoryBook.getNewEntityInstance());

        Book book2 = entityFactoryBook.getNewEntityInstance();
        book2.setTitle("test-title-findWithPredicateTest");

        final Book entitySaved2 = repository.saveAndFlush(book2);

        BooleanBuilder where = new BooleanBuilder();
        where.and(QBook.book.title.eq(book2.getTitle()));
        where.or(QBook.book.title.startsWith("test-title-startsWith"));
        where.and(QBook.book.id.eq(book2.getId()));

        if (book2.getCreatedBy() != null) {
            where.and(QBook.book.createdBy.id.eq(book2.getCreatedBy().getId()));
        }

        Iterable<Book> iterable = repository.findAll(where);

        assertAll(
                () -> assertThat(iterable).containsOnly(entitySaved2)
        );
    }

    @Profile("BookRepositoryTest")
    @TestConfiguration
    public static class IntegrationTestConfiguration {

        @Bean
        public EntityFactory<Book> getNewEntityInstance(TestEntityManager em) {
            return new EntityFactory<Book>() {
                @Override
                @Transactional(propagation = Propagation.REQUIRES_NEW)
                public Book getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    final User user = em.persistFlushFind(userNew);

                    String langName = "test-lang-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Lang langNew = new Lang();
                    langNew.setCode(langName);
                    final Lang lang = em.persistFlushFind(langNew);
                    final Work work = em.persistFlushFind(new Work());
                    String bookName = "test-book-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Book book = new Book();
                    book.setName(bookName);
                    book.setLang(lang);
                    book.setWork(work);
                    book.setCreatedBy(user);
                    book.setTitle("test-title-name" + UUID.randomUUID().toString().replaceAll("-", ""));
                    return book;
                }
            };
        }
    }
}