package com.lig.libby.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lig.libby.Main;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QUser;
import com.lig.libby.domain.User;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
@ActiveProfiles({"shellDisabled", "springDataJpa", "UserRepositoryTest"})
class UserRepositoryTest {


    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final UserRepository repository;
    private final EntityFactory<User> entityFactoryUser;

    @Autowired
    public UserRepositoryTest(@NonNull UserRepository repository, @NonNull EntityFactory<User> entityFactoryUser) {
        this.repository = repository;
        this.entityFactoryUser = entityFactoryUser;
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
        final User entity = entityFactoryUser.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final User deepEntityCopy = (User) SerializationUtils.clone(entity);

        final User entitySaved = repository.saveAndFind(entity);
        final User entityQueried = repository.findById(id).orElse(null);

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
        final User entitySaved = repository.saveAndFind(entityFactoryUser.getNewEntityInstance());
        final User deepEntitySavedCopy = (User) SerializationUtils.clone(entitySaved);
        entitySaved.setName("test-update-name");

        final User entityUpdated = repository.saveAndFind(entitySaved);

        deepEntitySavedCopy.setName(entitySaved.getName());

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
        final User entitySaved1 = repository.saveAndFind(entityFactoryUser.getNewEntityInstance());

        User user2 = entityFactoryUser.getNewEntityInstance();
        user2.setName("test-name-findWithPredicateTest");

        final User entitySaved2 = repository.saveAndFind(user2);

        BooleanBuilder where = new BooleanBuilder();
        where.and(QUser.user.name.eq(user2.getName()));
        where.or(QUser.user.name.startsWith("test-name-startsWith"));
        where.and(QUser.user.id.eq(user2.getId()));

        if (user2.getCreatedBy() != null) {
            where.and(QUser.user.lastUpdBy.id.eq(user2.getCreatedBy().getId()));
        }

        Iterable<User> iterable = repository.findAll(where);

        assertAll(
                () -> assertThat(iterable).containsOnly(entitySaved2)
        );
    }

    @Test
    void findByEmail() {
        final User entitySaved1 = repository.saveAndFind(entityFactoryUser.getNewEntityInstance());
        final User entitySaved2 = repository.saveAndFind(entityFactoryUser.getNewEntityInstance());
        Optional<User> findByEmailUser = repository.findByEmail(entitySaved1.getEmail());

        assertAll(
                () -> assertThat(findByEmailUser.orElse(null)).isEqualTo(entitySaved1)
        );
    }

    @Profile("UserRepositoryTest")
    @TestConfiguration
    public static class IntegrationTestConfiguration {


        @Bean
        public EntityFactory<User> getNewEntityInstance(MongoOperations em) {
            return new EntityFactory<User>() {
                @Override

                public User getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    em.save(userNew);
                    final User user = em.findById(userNew.getId(), User.class);

                    String userName2 = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew2 = new User();
                    userNew2.setName(userName2);
                    userNew2.setEmail(userName2 + "@libby.com");
                    userNew2.setProvider(Authority.AuthProvider.local);

                    userNew2.setLastUpdBy(user);

                    return userNew2;
                }
            };
        }
    }
}