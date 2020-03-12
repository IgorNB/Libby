package com.lig.libby.controller.adapter.adminui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.anonymousui.AuthControllerTest;
import com.lig.libby.controller.adapter.anonymousui.dto.LoginRequestDto;
import com.lig.libby.controller.adapter.userui.dto.TaskPublicDto;
import com.lig.libby.core.TestUtil;
import com.lig.libby.core.TestUtil.TestArgs;
import com.lig.libby.domain.*;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.*;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(properties = {"spring.batch.job.enabled=false", "spring.datasource.url= jdbc:h2:mem:TaskControllerUserStoryTest"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
public class TaskControllerUserStoryTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LangRepository langRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    public TaskRepository taskRepository;

    @Autowired
    private BookRepository bookRepository;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private javax.servlet.Filter springSecurityFilterChain;


    @BeforeEach
    public void setup() {
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, "admin@localhost", "admin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
    }

    @Test
    //@Transactional - spring integration used here which use separate transaction, so we'll create separate DB for this test with "spring.datasource.url=" instead of using Transactions
    void update() throws Exception {
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        User user = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);

        //пользователь создает заявку на новую книгу
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        Task task = TestUtil.createAndSaveTask(user, lang, work, taskRepository);

        //пользователь отправляет на рассмотрение заявку на новую книгу
        {
            Task createdTask = TestUtil.getDtoByIdByUser(user, "test", task, "/tasks/", mockMvc, Task.class);

            Task taskUserSubmitDtoRequest = (Task) SerializationUtils.clone(createdTask);

            assertThat(createdTask.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.SUBMITTED.name());
            taskUserSubmitDtoRequest.setCommand(Task.WorkflowStepEnum.SUBMITTED.name());
            TestUtil.putDtoByUser(user, "test", taskUserSubmitDtoRequest, "/tasks/", mockMvc, Task.class);
        }

        //администратор отправляет заявку на исправление пользователю
        {
            Task taskAdminSubmitDtoResponse = TestUtil.getDtoByIdByUser(admin, "admin", task, "/tasks/", mockMvc, Task.class);

            Task taskAdminEscalateDtoRequest = (Task) SerializationUtils.clone(taskAdminSubmitDtoResponse);

            assertThat(taskAdminSubmitDtoResponse.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.ESCALATED.name());
            taskAdminEscalateDtoRequest.setCommand(Task.WorkflowStepEnum.ESCALATED.name());
            TestUtil.putDtoByUser(admin, "admin", taskAdminEscalateDtoRequest, "/tasks/", mockMvc, Task.class);
        }

        //пользователь делает повторную отправку на рассмотрение
        {
            Task taskUserEscalateDtoResponse = TestUtil.getDtoByIdByUser(user, "test", task, "/tasks/", mockMvc, Task.class);

            Task taskUserSubmitAgainDtoRequest = (Task) SerializationUtils.clone(taskUserEscalateDtoResponse);

            assertThat(taskUserEscalateDtoResponse.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.SUBMITTED.name());

            taskUserSubmitAgainDtoRequest.setCommand(Task.WorkflowStepEnum.SUBMITTED.name());
            TestUtil.putDtoByUser(user, "test", taskUserSubmitAgainDtoRequest, "/tasks/", mockMvc, Task.class);
        }

        //администратор принимает повторную заявку - создается книга на основе заявки
        Task resultDto;
        {
            Task taskAdminSubmitAgainDtoResponse = TestUtil.getDtoByIdByUser(admin, "admin", task, "/tasks/", mockMvc, Task.class);

            Task taskAdminApproveDtoRequest = (Task) SerializationUtils.clone(taskAdminSubmitAgainDtoResponse);

            assertThat(taskAdminSubmitAgainDtoResponse.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.APPROVED.name());
            taskAdminApproveDtoRequest.setCommand(Task.WorkflowStepEnum.APPROVED.name());
            resultDto = TestUtil.putDtoByUser(admin, "admin", taskAdminApproveDtoRequest, "/tasks/", mockMvc, Task.class);
        }

        //успешно создана книга по заявке
        Iterable<Book> createdBooks = bookRepository.findAll(new BooleanBuilder().and(QBook.book.title.eq(task.getBookTitle())));

        assertAll(
                () -> assertThat(createdBooks).hasSize(1),
                () -> assertThat(resultDto).hasFieldOrPropertyWithValue("points", BigInteger.valueOf(10))
        );

    }
}
