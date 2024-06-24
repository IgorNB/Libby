package com.lig.libby.controller.adapter.userui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.anonymousui.AuthControllerTest;
import com.lig.libby.controller.adapter.anonymousui.dto.LoginRequestDto;
import com.lig.libby.controller.adapter.userui.dto.LangPublicDto;
import com.lig.libby.controller.adapter.userui.dto.TaskPublicDto;
import com.lig.libby.controller.adapter.userui.dto.UserPublicDto;
import com.lig.libby.controller.adapter.userui.dto.WorkPublicDto;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
public class TaskPublicControllerTest {

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

    public void findOneTest() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        Task task = TestUtil.createAndSaveTask(user, lang, work, taskRepository);

        TaskPublicDto assertResponce = new TaskPublicDto();
        assertResponce.setId(task.getId());

        assertResponce.setBookName(task.getBookName());

        LangPublicDto langPublicDto = new LangPublicDto();
        langPublicDto.setId(task.getBookLang().getId());
        assertResponce.setBookLang(langPublicDto);

        WorkPublicDto workPublicDto = new WorkPublicDto();
        workPublicDto.setId(task.getBookWork().getId());
        assertResponce.setBookWork(workPublicDto);

        assertResponce.setBookTitle(task.getBookTitle());
        assertResponce.setWorkflowStep(task.getWorkflowStep());
        assertResponce.setVersion(task.getVersion());

        UserPublicDto userPublicDto = new UserPublicDto();
        userPublicDto.setId(task.getCreatedBy().getId());
        assertResponce.setCreatedBy(userPublicDto);
        assertResponce.setLastUpdBy(userPublicDto);

        UserPublicDto assertPublicDto = new UserPublicDto();
        assertPublicDto.setId(task.getAssignee().getId());
        assertResponce.setAssignee(assertPublicDto);

        //fields that assigned in dto in other layers
        assertResponce.setAvailableCommands(Arrays.asList(Task.WorkflowStepEnum.SUBMITTED.name()));
        TaskPublicDto response = TestUtil.getDtoByIdByUser(user, "test", task, "/tasksPublic/", mockMvc, TaskPublicDto.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }


    @TestFactory
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    Stream<DynamicTest> dynamicFindAllTest() {
        List<TestArgs<User, String, String, List<Task>, String, String, String>> inputList;
        {
            User admin = userRepository.findByEmail("admin@localhost").orElse(null);
            User user1 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
            User user2 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

            Lang task12Lang = TestUtil.createAndSaveLang(langRepository);
            Work task13Work = TestUtil.createAndSaveWork(workRepository);
            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user1.getEmail(), "test");
            Task task1 = TestUtil.createAndSaveTask(user1, task12Lang, task13Work, taskRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user2.getEmail(), "test");
            Task task2 = TestUtil.createAndSaveTask(user2, task12Lang, TestUtil.createAndSaveWork(workRepository), taskRepository);
            Task task3 = TestUtil.createAndSaveTask(user2, TestUtil.createAndSaveLang(langRepository), task13Work, taskRepository);

            inputList = Arrays.asList(
                    //search all && role access
                    new TestArgs<>(user1, "test", "/tasksPublic/",
                            Arrays.asList(task1), null, null, null),
                    new TestArgs<>(user2, "test", "/tasksPublic/",
                            Arrays.asList(task2, task3), null, null, null),
                    new TestArgs<>(admin, "admin", "/tasksPublic/",
                            Arrays.asList(task1, task2, task3), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/tasksPublic/?bookLang.id=" + task12Lang.getId(),
                            Arrays.asList(task1), null, null, null),
                    new TestArgs<>(user2, "test", "/tasksPublic/?bookLang.id=" + task12Lang.getId(),
                            Arrays.asList(task2), null, null, null),
                    new TestArgs<>(admin, "admin", "/tasksPublic/?bookLang.id=" + task12Lang.getId(),
                            Arrays.asList(task1, task2), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/tasksPublic/?bookWork.id=" + task13Work.getId(),
                            Arrays.asList(task1), null, null, null),
                    new TestArgs<>(user2, "test", "/tasksPublic/?bookWork.id=" + task13Work.getId(),
                            Arrays.asList(task3), null, null, null),
                    new TestArgs<>(admin, "admin", "/tasksPublic/?bookWork.id=" + task13Work.getId(),
                            Arrays.asList(task1, task3), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/tasksPublic/?id=" + task1.getId(),
                            Arrays.asList(task1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/tasksPublic/?id=" + task1.getId() + "&id=" + task2.getId(),
                            Arrays.asList(task1, task2), null, null, null)
            );
        }

        return inputList.stream()
                .map(test -> DynamicTest.dynamicTest(
                        " When call url: " + test.getArgC() +
                                " by user: " + test.getArgA().getEmail() +
                                " loggined with password: " + test.getArgB() +
                                " then see only tasks: " + test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.joining(",")),
                        () -> {
                            String responsePageJson = TestUtil.getDtoPageByQueryParamsByUser(test.getArgA(), test.getArgB(), test.getArgC(), mockMvc);
                            TestUtil.HelperPage<TaskPublicDto> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<TaskPublicDto>>() {
                            }).readValue(responsePageJson);
                            assertAll(
                                    () -> assertThat(responsePage.stream().map(PersistentObject::getId).collect(Collectors.toList())).
                                            containsExactlyInAnyOrderElementsOf(test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.toList()))
                            );
                        }
                        )
                );
    }


    @Test
    void create() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);

        TaskPublicDto requestDto = new TaskPublicDto();

        LangPublicDto langPublicDto = new LangPublicDto();
        langPublicDto.setId(lang.getId());
        requestDto.setBookLang(langPublicDto);

        WorkPublicDto workPublicDto = new WorkPublicDto();
        workPublicDto.setId(work.getId());
        requestDto.setBookWork(workPublicDto);

        requestDto.setId("test-task-" + UUID.randomUUID().toString().replaceAll("-", ""));
        requestDto.setBookName("book-name-" + requestDto.getId());
        requestDto.setBookTitle("book-title-" + requestDto.getId());

        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password("test").build());

        TaskPublicDto responseDto = TestUtil.postDtoByUser(user, "test", requestDto, "/tasksPublic/", mockMvc, TaskPublicDto.class);

        //copy request and enrich with fields that backend should set itself
        TaskPublicDto expectedResponseDTO = (TaskPublicDto) SerializationUtils.clone(requestDto);
        expectedResponseDTO.setVersion(0);

        UserPublicDto userPublicDto = new UserPublicDto();
        userPublicDto.setId(user.getId());
        expectedResponseDTO.setAssignee(userPublicDto);
        expectedResponseDTO.setCreatedBy(userPublicDto);
        expectedResponseDTO.setLastUpdBy(userPublicDto);
        expectedResponseDTO.setWorkflowStep(Task.WorkflowStepEnum.INIT);
        expectedResponseDTO.setAvailableCommands(Arrays.asList(Task.WorkflowStepEnum.SUBMITTED.name()));

        //copy responce fields we cannot predict
        expectedResponseDTO.setCreatedDate(responseDto.getCreatedDate());
        expectedResponseDTO.setUpdatedDate(responseDto.getUpdatedDate());


        assertAll(
                () -> assertThat(responseDto).isEqualToComparingFieldByField(expectedResponseDTO)
        );

    }

    @Test
    void update() throws Exception {
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);

        //пользователь создает заявку на новую книгу
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        Task task = TestUtil.createAndSaveTask(user, lang, work, taskRepository);

        //пользователь отправляет на рассмотрение заявку на новую книгу
        {
            TaskPublicDto createdTaskDto = TestUtil.getDtoByIdByUser(user, "test", task, "/tasksPublic/", mockMvc, TaskPublicDto.class);

            TaskPublicDto taskUserSubmitDtoRequest = (TaskPublicDto) SerializationUtils.clone(createdTaskDto);

            assertThat(createdTaskDto.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.SUBMITTED.name());
            taskUserSubmitDtoRequest.setCommand(Task.WorkflowStepEnum.SUBMITTED.name());
            TestUtil.putDtoByUser(user, "test", taskUserSubmitDtoRequest, "/tasksPublic/", mockMvc, TaskPublicDto.class);
        }

        //администратор отправляет заявку на исправление пользователю
        {
            TaskPublicDto taskAdminSubmitDtoResponse = TestUtil.getDtoByIdByUser(admin, "admin", task, "/tasksPublic/", mockMvc, TaskPublicDto.class);

            TaskPublicDto taskAdminEscalateDtoRequest = (TaskPublicDto) SerializationUtils.clone(taskAdminSubmitDtoResponse);

            assertThat(taskAdminSubmitDtoResponse.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.ESCALATED.name());
            taskAdminEscalateDtoRequest.setCommand(Task.WorkflowStepEnum.ESCALATED.name());
            TestUtil.putDtoByUser(admin, "admin", taskAdminEscalateDtoRequest, "/tasksPublic/", mockMvc, TaskPublicDto.class);
        }

        //пользователь делает повторную отправку на рассмотрение
        {
            TaskPublicDto taskUserEscalateDtoResponse = TestUtil.getDtoByIdByUser(user, "test", task, "/tasksPublic/", mockMvc, TaskPublicDto.class);

            TaskPublicDto taskUserSubmitAgainDtoRequest = (TaskPublicDto) SerializationUtils.clone(taskUserEscalateDtoResponse);

            assertThat(taskUserEscalateDtoResponse.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.SUBMITTED.name());

            taskUserSubmitAgainDtoRequest.setCommand(Task.WorkflowStepEnum.SUBMITTED.name());
            TestUtil.putDtoByUser(user, "test", taskUserSubmitAgainDtoRequest, "/tasksPublic/", mockMvc, TaskPublicDto.class);
        }

        //администратор принимает делает повторную заявку - создается книга на основе заявки
        {
            TaskPublicDto taskAdminSubmitAgainDtoResponse = TestUtil.getDtoByIdByUser(admin, "admin", task, "/tasksPublic/", mockMvc, TaskPublicDto.class);

            TaskPublicDto taskAdminApproveDtoRequest = (TaskPublicDto) SerializationUtils.clone(taskAdminSubmitAgainDtoResponse);

            assertThat(taskAdminSubmitAgainDtoResponse.getAvailableCommands().stream()).contains(Task.WorkflowStepEnum.APPROVED.name());
            taskAdminApproveDtoRequest.setCommand(Task.WorkflowStepEnum.APPROVED.name());
            TestUtil.putDtoByUser(admin, "admin", taskAdminApproveDtoRequest, "/tasksPublic/", mockMvc, TaskPublicDto.class);
        }

        //успешно создана книга по заявке
        Iterable<Book> createdBooks = bookRepository.findAll(new BooleanBuilder().and(QBook.book.title.eq(task.getBookTitle())));

        assertAll(
                () -> assertThat(createdBooks).hasSize(1)
        );

    }


    @Test
    void delete() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        Task task = TestUtil.createAndSaveTask(user, lang, work, taskRepository);

        Gson gson = new Gson();
        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password("test").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/tasksPublic/" + task.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        assertAll(
                () -> assertThat(taskRepository.findById(task.getId())).isEmpty()
        );
    }
}
