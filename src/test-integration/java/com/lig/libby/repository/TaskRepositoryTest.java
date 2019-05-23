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
@ActiveProfiles({"shellDisabled", "springDataJpa", "TaskRepositoryTest"})
class TaskRepositoryTest {

    public final MongoOperations mongoOperations;

    public static final String OPERATION_NOT_SUPPORTED = "Operation not supported";
    public final TaskRepository repository;
    private final EntityFactory<Task> entityFactoryTask;

    @Autowired
    public TaskRepositoryTest(@NonNull MongoOperations mongoOperations, @NonNull TaskRepository repository, @NonNull EntityFactory<Task> entityFactoryTask) {
        this.mongoOperations = mongoOperations;
        this.repository = repository;
        this.entityFactoryTask = entityFactoryTask;
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
        final Task entity = entityFactoryTask.getNewEntityInstance();
        String id = entity.getId();
        Integer version = entity.getVersion();

        final Task deepEntityCopy = (Task) SerializationUtils.clone(entity);

        final Task entitySaved = repository.saveAndFind(entity);
        final Task entityQueried = repository.findById(id).orElse(null);

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
        final Task entitySaved = repository.saveAndFind(entityFactoryTask.getNewEntityInstance());
        final Task deepEntitySavedCopy = (Task) SerializationUtils.clone(entitySaved);
        entitySaved.setBookTitle("test-update-title");

        final Task entityUpdated = repository.saveAndFind(entitySaved);

        deepEntitySavedCopy.setBookTitle(entitySaved.getBookTitle());
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
        final Task entitySaved1 = repository.saveAndFind(entityFactoryTask.getNewEntityInstance());

        Task task2 = entityFactoryTask.getNewEntityInstance();
        task2.setBookTitle("test-title-findWithPredicateTest");

        final Task entitySaved2 = repository.saveAndFind(task2);

        BooleanBuilder where = new BooleanBuilder();
        where.and(QTask.task.bookTitle.eq(task2.getBookTitle()));
        where.or(QTask.task.bookTitle.startsWith("test-title-startsWith"));
        where.and(QTask.task.id.eq(task2.getId()));

        if (task2.getCreatedBy() != null) {
            where.and(QTask.task.createdBy.id.eq(task2.getCreatedBy().getId()));
        }

        Iterable<Task> iterable = repository.findAll(where);

        assertAll(
                () -> assertThat(iterable).containsOnly(entitySaved2)
        );
    }

    @Profile("TaskRepositoryTest")
    @TestConfiguration
    public static class IntegrationTestConfiguration {


        @Bean
        public EntityFactory<Task> getNewEntityInstance(MongoOperations em) {
            return new EntityFactory<Task>() {
                @Override

                public Task getNewEntityInstance() {
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
                    String taskName = "test-task-name" + UUID.randomUUID().toString().replaceAll("-", "");
                    Task task = new Task();
                    task.setBookName(taskName);
                    task.setBookLang(lang);
                    task.setBookWork(work);
                    task.setCreatedBy(user);
                    task.setBookTitle("test-title-name" + UUID.randomUUID().toString().replaceAll("-", ""));
                    task.setWorkflowStep(Task.WorkflowStepEnum.INIT);
                    task.setAssignee(user);
                    return task;
                }
            };
        }
    }
}