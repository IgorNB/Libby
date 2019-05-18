package com.lig.libby.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.Lang;
import com.lig.libby.domain.QLang;
import com.lig.libby.domain.User;
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
@ActiveProfiles({"shellDisabled", "springDataJpa", "LangRepositoryTest"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LangRepositoryTest {

    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final LangRepository repository;
    private final TestEntityManager em;
    private final EntityManager entityManager;
    private final EntityFactory<Lang> entityFactoryLang;

    @Autowired
    public LangRepositoryTest(@NonNull LangRepository repository, @NonNull TestEntityManager em, @NonNull EntityFactory<Lang> entityFactoryLang, @NonNull EntityManager entityManager) {
        this.repository = repository;
        this.em = em;
        this.entityFactoryLang = entityFactoryLang;
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
        final Lang entity = entityFactoryLang.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Lang deepEntityCopy = (Lang) SerializationUtils.clone(entity);

        final Lang entitySaved = repository.saveAndFlush(entity);
        final Lang entityQueried = repository.findById(id).orElse(null);

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
        final Lang entitySaved = repository.saveAndFlush(entityFactoryLang.getNewEntityInstance());
        final Lang deepEntitySavedCopy = (Lang) SerializationUtils.clone(entitySaved);
        entitySaved.setCode("test-update-code");

        final Lang entityUpdated = repository.saveAndFlush(entitySaved);

        deepEntitySavedCopy.setCode(entitySaved.getCode());

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
        final Lang entitySaved1 = repository.saveAndFlush(entityFactoryLang.getNewEntityInstance());

        Lang lang2 = entityFactoryLang.getNewEntityInstance();
        lang2.setCode("test-code-findWithPredicateTest");

        final Lang entitySaved2 = repository.saveAndFlush(lang2);

        BooleanBuilder where = new BooleanBuilder();
        where.and(QLang.lang.code.eq(lang2.getCode()));
        where.or(QLang.lang.code.startsWith("test-code-startsWith"));
        where.and(QLang.lang.id.eq(lang2.getId()));

        if (lang2.getCreatedBy() != null) {
            where.and(QLang.lang.lastUpdBy.id.eq(lang2.getCreatedBy().getId()));
        }

        Iterable<Lang> iterable = repository.findAll(where);

        assertAll(
                () -> assertThat(iterable).containsOnly(entitySaved2)
        );
    }

    @Profile("LangRepositoryTest")
    @TestConfiguration
    public static class IntegrationTestConfiguration {


        @Bean
        public EntityFactory<Lang> getNewEntityInstance(TestEntityManager em) {
            return new EntityFactory<Lang>() {
                @Override
                @Transactional(propagation = Propagation.REQUIRES_NEW)
                public Lang getNewEntityInstance() {
                    String userName = "test-user-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    User userNew = new User();
                    userNew.setName(userName);
                    userNew.setEmail(userName + "@libby.com");
                    userNew.setProvider(Authority.AuthProvider.local);
                    final User user = em.persistFlushFind(userNew);

                    String langName = "test-lang-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Lang langNew = new Lang();
                    langNew.setCode(langName);
                    langNew.setLastUpdBy(user);

                    return langNew;
                }
            };
        }
    }
}