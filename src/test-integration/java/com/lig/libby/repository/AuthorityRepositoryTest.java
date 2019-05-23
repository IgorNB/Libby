package com.lig.libby.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lig.libby.Main;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QAuthority;
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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslMongoPredicateExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
@ActiveProfiles({"shellDisabled", "springDataJpa", "AuthorityRepositoryTest"})
class AuthorityRepositoryTest {


    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final AuthorityRepository repository;
    private final EntityFactory<Authority> entityFactoryAuthority;

    @Autowired
    public AuthorityRepositoryTest(@NonNull AuthorityRepository repository, @NonNull EntityFactory<Authority> entityFactoryAuthority) {
        this.repository = repository;
        this.entityFactoryAuthority = entityFactoryAuthority;
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
        final Authority entity = entityFactoryAuthority.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Authority deepEntityCopy = (Authority) SerializationUtils.clone(entity);

        final Authority entitySaved = repository.saveAndFind(entity);
        final Authority entityQueried = repository.findById(id).orElse(null);

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
        final Authority entitySaved = repository.saveAndFind(entityFactoryAuthority.getNewEntityInstance());
        final Authority deepEntitySavedCopy = (Authority) SerializationUtils.clone(entitySaved);
        entitySaved.setLastUpdBy(entityFactoryAuthority.getNewEntityInstance().getLastUpdBy());

        final Authority entityUpdated = repository.saveAndFind(entitySaved);


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
        final Authority entitySaved1 = repository.saveAndFind(entityFactoryAuthority.getNewEntityInstance());

        Authority authority2 = entityFactoryAuthority.getNewEntityInstance();

        final Authority entitySaved2 = repository.saveAndFind(authority2);

        BooleanBuilder where = new BooleanBuilder();

        where.and(QAuthority.authority.id.eq(authority2.getId()));

        if (authority2.getCreatedBy() != null) {
            where.and(QAuthority.authority.lastUpdBy.id.eq(authority2.getCreatedBy().getId()));
        }

        Iterable<Authority> iterable = repository.findAll(where);

        assertAll(
                () -> assertThat(iterable).containsOnly(entitySaved2)
        );
    }

    @Test
    void getAuthorityByName() {

        final Authority entitySaved1 = repository.saveAndFind(entityFactoryAuthority.getNewEntityInstance());

        Authority authority2 = entityFactoryAuthority.getNewEntityInstance();

        final Authority entitySaved2 = repository.saveAndFind(authority2);

        Authority authorityQueried = repository.getAuthorityByName(entitySaved2.getName());

        assertAll(
                () -> assertThat(authorityQueried).isEqualTo(entitySaved2)
        );

    }

    @Profile("AuthorityRepositoryTest")
    @TestConfiguration
    public static class IntegrationTestConfiguration {

        @Bean
        @Primary
        public AuditorAware<User> auditorProvider(@Autowired MongoOperations entityManager) {
            return () -> {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                return Optional.ofNullable(securityContext.getAuthentication())
                        .map(authentication -> {
                            /*if (authentication.getPrincipal() instanceof UserDetails) {
                                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                                return entityManager.getReference(User.class, userDetails.getUsername());
                            }*/
                            return null; //in dataJPA tests we do not log createdBy and updatedBy
                        });
            };
        }

        @Bean
        public EntityFactory<Authority> getNewEntityInstance(MongoOperations em) {
            return new EntityFactory<Authority>() {
                @Override

                public Authority getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    userNew.setPassword("test-encrypted-password" + userName);
                    em.save(userNew);
                    final User user = em.findById(userNew.getId(), User.class);

                    String authorityName2 = "test-authority-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Authority authorityNew2 = new Authority();
                    authorityNew2.setLastUpdBy(user);
                    authorityNew2.setName(authorityName2);
                    return authorityNew2;
                }
            };
        }
    }
}