package com.lig.libby.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lig.libby.Main;
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

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslMongoPredicateExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
@ActiveProfiles({"shellDisabled", "springDataJpa", "CommentRepositoryTest"})
class CommentRepositoryTest {


    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final CommentRepository repository;
    private final EntityFactory<Comment> entityFactoryComment;

    @Autowired
    public CommentRepositoryTest(@NonNull CommentRepository repository, @NonNull EntityFactory<Comment> entityFactoryComment) {
        this.repository = repository;
        this.entityFactoryComment = entityFactoryComment;
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
        final Comment entity = entityFactoryComment.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Comment deepEntityCopy = (Comment) SerializationUtils.clone(entity);

        final Comment entitySaved = repository.saveAndFind(entity);
        final Comment entityQueried = repository.findById(id).orElse(null);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        deepEntityCopy.setVersion(0);

        deepEntityCopy.setCreatedBy(entitySaved.getCreatedBy());
        deepEntityCopy.setCreatedDate(entitySaved.getCreatedDate());

        deepEntityCopy.setUpdatedDate(entitySaved.getUpdatedDate());
        deepEntityCopy.setLastUpdBy(entitySaved.getLastUpdBy());
        assertAll(
                () -> assertThat(deepEntityCopy).isEqualToComparingFieldByFieldRecursively(entitySaved),
                () -> assertThat(deepEntityCopy).isEqualToComparingFieldByFieldRecursively(entityQueried)/*,
                () -> assertThat(gson.toJson(deepEntityCopy).toString()).isEqualTo(gson.toJson(entitySaved).toString()),
                () -> assertThat(gson.toJson(deepEntityCopy).toString()).isEqualTo(gson.toJson(entityQueried).toString())*/
        );
    }

    @Test

    public void updateAndQueryTest() {
        final Comment entitySaved = repository.saveAndFind(entityFactoryComment.getNewEntityInstance());
        final Comment deepEntitySavedCopy = (Comment) SerializationUtils.clone(entitySaved);
        entitySaved.setLastUpdBy(entityFactoryComment.getNewEntityInstance().getLastUpdBy());

        final Comment entityUpdated = repository.saveAndFind(entitySaved);


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
        final Comment entitySaved1 = repository.saveAndFind(entityFactoryComment.getNewEntityInstance());

        Comment comment2 = entityFactoryComment.getNewEntityInstance();

        final Comment entitySaved2 = repository.saveAndFind(comment2);

        BooleanBuilder where = new BooleanBuilder();

        where.and(QComment.comment.id.eq(comment2.getId()));

        if (comment2.getCreatedBy() != null) {
            where.and(QComment.comment.lastUpdBy.id.eq(comment2.getCreatedBy().getId()));
        }

        Iterable<Comment> iterable = repository.findAll(where);

        assertAll(
                () -> assertThat(iterable).containsOnly(entitySaved2)
        );
    }

    @Profile("CommentRepositoryTest")
    @TestConfiguration
    public static class IntegrationTestConfiguration {


        @Bean
        public EntityFactory<Comment> getNewEntityInstance(MongoOperations em) {
            return new EntityFactory<Comment>() {
                @Override

                public Comment getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    em.save(userNew);
                    final User userSaved = em.findById(userNew.getId(),User.class);

                    String bookName = "test-book-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Book book = new Book();
                    book.setName(bookName);
                    book.setTitle(bookName + "_title");
                    Work workNew = new Work();
                    em.save(workNew);
                    final Work work= em.findById(workNew.getId(),Work.class);
                    book.setWork(work);

                    em.save(book);

                    final Book bookSaved = em.findById(book.getId(),Book.class);

                    String commentName2 = "test-comment-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Comment commentNew2 = new Comment();
                    commentNew2.setLastUpdBy(userSaved);
                    commentNew2.setBook(bookSaved);
                    return commentNew2;
                }
            };
        }
    }
}