package com.lig.libby.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lig.libby.Main;
import com.lig.libby.domain.*;
import com.lig.libby.repository.common.EntityFactory;
import com.querydsl.core.BooleanBuilder;
import lombok.NonNull;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslMongoPredicateExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
@ActiveProfiles({"shellDisabled", "springDataJpa", "BookRepositoryTest"})
class BookRepositoryTest {

    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final BookRepository repository;
    private final EntityFactory<Book> entityFactoryBook;

    @Autowired
    public BookRepositoryTest(@NonNull BookRepository repository, @NonNull EntityFactory<Book> entityFactoryBook) {
        this.repository = repository;
        this.entityFactoryBook = entityFactoryBook;
    }

    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(repository instanceof QuerydslMongoPredicateExecutor
                || AopUtils.getTargetClass(repository).equals(QuerydslMongoPredicateExecutor.class)
                || repository.toString().contains(QuerydslMongoPredicateExecutor.class.getName())
        ).isTrue();
    }

    @Test

    public void saveAndQueryTest() {
        final Book entity = entityFactoryBook.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Book deepEntityCopy = (Book) SerializationUtils.clone(entity);

        final Book entitySaved = repository.saveAndFind(entity);
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

    public void updateAndQueryTest() {
        final Book entitySaved = repository.saveAndFind(entityFactoryBook.getNewEntityInstance());
        final Book deepEntitySavedCopy = (Book) SerializationUtils.clone(entitySaved);
        entitySaved.setTitle("test-update-title");

        final Book entityUpdated = repository.saveAndFind(entitySaved);

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

    public void findWithPredicateTest() {
        final Book entitySaved1 = repository.saveAndFind(entityFactoryBook.getNewEntityInstance());

        Book book2 = entityFactoryBook.getNewEntityInstance();
        book2.setTitle("test-title-findWithPredicateTest");

        final Book entitySaved2 = repository.saveAndFind(book2);

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
        public EntityFactory<Book> getNewEntityInstance(MongoOperations em) {
            return new EntityFactory<Book>() {
                @Override

                public Book getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    em.save(userNew);
                    final User user = em.findById(userNew.getId(), User.class);

                    String langName = "test-lang-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Lang langNew = new Lang();
                    langNew.setCode(langName);
                    em.save(langNew);
                    final Lang lang = em.findById(langNew.getId(), Lang.class);

                    Work workNew = new Work();
                    em.save(workNew);
                    final Work work = em.findById(workNew.getId(), Work.class);
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