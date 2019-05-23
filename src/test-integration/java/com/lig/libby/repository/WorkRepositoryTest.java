package com.lig.libby.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lig.libby.Main;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QWork;
import com.lig.libby.domain.User;
import com.lig.libby.domain.Work;
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
@ActiveProfiles({"shellDisabled", "springDataJpa", "WorkRepositoryTest"})
class WorkRepositoryTest {


    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final WorkRepository repository;
    private final EntityFactory<Work> entityFactoryWork;

    @Autowired
    public WorkRepositoryTest(@NonNull WorkRepository repository, @NonNull EntityFactory<Work> entityFactoryWork) {
        this.repository = repository;
        this.entityFactoryWork = entityFactoryWork;
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
        final Work entity = entityFactoryWork.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Work deepEntityCopy = (Work) SerializationUtils.clone(entity);

        final Work entitySaved = repository.saveAndFind(entity);
        final Work entityQueried = repository.findById(id).orElse(null);

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
        final Work entitySaved = repository.saveAndFind(entityFactoryWork.getNewEntityInstance());
        final Work deepEntitySavedCopy = (Work) SerializationUtils.clone(entitySaved);
        entitySaved.setLastUpdBy(entityFactoryWork.getNewEntityInstance().getLastUpdBy());

        final Work entityUpdated = repository.saveAndFind(entitySaved);

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
        final Work entitySaved1 = repository.saveAndFind(entityFactoryWork.getNewEntityInstance());

        Work work2 = entityFactoryWork.getNewEntityInstance();

        final Work entitySaved2 = repository.saveAndFind(work2);

        BooleanBuilder where = new BooleanBuilder();

        where.and(QWork.work.id.eq(work2.getId()));

        if (work2.getCreatedBy() != null) {
            where.and(QWork.work.lastUpdBy.id.eq(work2.getCreatedBy().getId()));
        }

        Iterable<Work> iterable = repository.findAll(where);

        assertAll(
                () -> assertThat(iterable).containsOnly(entitySaved2)
        );
    }

    @Profile("WorkRepositoryTest")
    @TestConfiguration
    public static class IntegrationTestConfiguration {


        @Bean
        public EntityFactory<Work> getNewEntityInstance(MongoOperations em) {
            return new EntityFactory<Work>() {
                @Override

                public Work getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    em.save(userNew);
                    final User user = em.findById(userNew.getId(), User.class);

                    String workName2 = "test-work-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Work workNew2 = new Work();
                    workNew2.setLastUpdBy(user);
                    return workNew2;
                }
            };
        }
    }
}