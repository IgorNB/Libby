package com.lig.libby.controller.adapter.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.anonymousui.AuthControllerTest;
import com.lig.libby.controller.adapter.anonymousui.dto.LoginRequestDto;
import com.lig.libby.core.TestUtil;
import com.lig.libby.core.TestUtil.TestArgs;
import com.lig.libby.domain.*;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.*;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.SerializationUtils;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
@Log4j2
public class ActuatorConfigTest {

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
    @Transactional
    void totalTasksSubmittedHealthActuatorEndpointTest() throws Exception {
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        User user = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);

        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        Task task = TestUtil.createAndSaveTask(user, lang, work, taskRepository);


        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.details.totalTasksSubmitted.details.value").value("0"));

        task.setWorkflowStep(Task.WorkflowStepEnum.SUBMITTED);
        taskRepository.saveAndFlush(task);

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.details.totalTasksSubmitted.details.value").value("1"));
    }

    @Test
    @Transactional
    void liquibaseActuatorEndpointTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/liquibase")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.contexts.application.liquibaseBeans.liquibase.changeSets").exists());

    }

    @Test
    @Transactional
    void logfileEndpointTest() throws Exception {

        String errorMessage = UUID.randomUUID().toString();
        log.error(errorMessage);
        
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/logfile")
                .accept(MediaType.ALL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(errorMessage)));

    }
}
