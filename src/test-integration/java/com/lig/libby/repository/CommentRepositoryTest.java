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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = {DataJpaAuditConfig.class}))
@ActiveProfiles({"shellDisabled", "springDataJpa", "CommentRepositoryTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {


    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final CommentRepository repository;
    private final TestEntityManager em;
    private final EntityManager entityManager;
    private final EntityFactory<Comment> entityFactoryComment;

    @Autowired
    public CommentRepositoryTest(@NonNull CommentRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Comment> entityFactoryComment, @NonNull EntityManager entityManager) {
        this.repository = repository;
        this.em = em;
        this.entityFactoryComment = entityFactoryComment;
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
        final Comment entity = entityFactoryComment.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Comment deepEntityCopy = (Comment) SerializationUtils.clone(entity);

        final Comment entitySaved = repository.saveAndFlush(entity);
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
    @Transactional
    public void updateAndQueryTest() {
        final Comment entitySaved = repository.saveAndFlush(entityFactoryComment.getNewEntityInstance());
        final Comment deepEntitySavedCopy = (Comment) SerializationUtils.clone(entitySaved);
        entitySaved.setLastUpdBy(entityFactoryComment.getNewEntityInstance().getLastUpdBy());

        final Comment entityUpdated = repository.saveAndFlush(entitySaved);


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
        final Comment entitySaved1 = repository.saveAndFlush(entityFactoryComment.getNewEntityInstance());

        Comment comment2 = entityFactoryComment.getNewEntityInstance();

        final Comment entitySaved2 = repository.saveAndFlush(comment2);

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
        public EntityFactory<Comment> getNewEntityInstance(TestEntityManager em) {
            return new EntityFactory<Comment>() {
                @Override
                @Transactional(propagation = Propagation.REQUIRES_NEW)
                public Comment getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    final User userSaved = em.persistFlushFind(userNew);

                    String bookName = "test-book-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Book book = new Book();
                    book.setName(bookName);
                    book.setTitle(bookName + "_title");
                    book.setWork(em.persistFlushFind(new Work()));
                    final Book bookSaved = em.persistFlushFind(book);

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