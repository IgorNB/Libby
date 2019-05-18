package com.lig.libby.controller.adapter.adminui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.anonymousui.AuthControllerTest;
import com.lig.libby.controller.adapter.anonymousui.dto.LoginRequestDto;
import com.lig.libby.core.TestUtil;
import com.lig.libby.core.TestUtil.TestArgs;
import com.lig.libby.domain.QUser;
import com.lig.libby.domain.User;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.AuthorityRepository;
import com.lig.libby.repository.UserRepository;
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

import java.util.Arrays;
import java.util.List;
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
public class UserControllerTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepository userRepository;

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
    public void findOneTest() throws Exception {
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, "admin@localhost", "admin");
        User user = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
        User userTest = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);

        User assertResponce = new User();
        assertResponce.setId(userTest.getId());
        assertResponce.setName(userTest.getName());
        assertResponce.setEmail(userTest.getEmail());
        assertResponce.setEmailVerified(userTest.getEmailVerified());
        assertResponce.setProvider(userTest.getProvider());
        assertResponce.setImageUrl(userTest.getImageUrl());

        assertResponce.setVersion(userTest.getVersion());

        User userTestto = new User();
        userTestto.setId(userTest.getCreatedBy().getId());
        assertResponce.setCreatedBy(userTestto);
        assertResponce.setLastUpdBy(userTestto);

        User response = TestUtil.getDtoByIdByUser(user, "test", userTest, "/users/", mockMvc, User.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }


    @Transactional
    @TestFactory
    Stream<DynamicTest> dynamicFindAllTest() {
        List<TestArgs<User, String, String, List<User>, String, String, String>> inputList;
        {

            User admin = userRepository.findByEmail("admin@localhost").orElse(null);
            User user1 = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
            User user2 = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user1.getEmail(), "test");
            User userTest1 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user2.getEmail(), "test");
            User userTest2 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
            User userTest3 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

            inputList = Arrays.asList(
                    //search all && role access
                    new TestArgs<>(user1, "test", "/users/",
                            Arrays.asList(admin, user1, user2, userTest1, userTest2, userTest3), null, null, null),
                    new TestArgs<>(user2, "test", "/users/",
                            Arrays.asList(admin, user1, user2, userTest1, userTest2, userTest3), null, null, null),
                    new TestArgs<>(admin, "admin", "/users/",
                            Arrays.asList(admin, user1, user2, userTest1, userTest2, userTest3), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/users/?createdBy.id=" + userTest1.getCreatedBy().getId(),
                            Arrays.asList(userTest1), null, null, null),
                    new TestArgs<>(user2, "test", "/users/?createdBy.id=" + userTest2.getCreatedBy().getId(),
                            Arrays.asList(userTest2, userTest3), null, null, null),
                    new TestArgs<>(admin, "admin", "/users/?createdBy.id=" + userTest3.getCreatedBy().getId(),
                            Arrays.asList(userTest2, userTest3), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/users/?email=" + userTest1.getEmail(),
                            Arrays.asList(userTest1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/users/?id=" + userTest1.getId() + "&id=" + userTest2.getId(),
                            Arrays.asList(userTest1, userTest2), null, null, null)
            );
        }

        return inputList.stream()
                .map(test -> DynamicTest.dynamicTest(
                        " When call url: " + test.getArgC() +
                                " by user: " + test.getArgA().getEmail() +
                                " loggined with password: " + test.getArgB() +
                                " then see only userTests: " + test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.joining(",")),
                        () -> {

                            String responsePageJson = TestUtil.getDtoPageByQueryParamsByUser(test.getArgA(), test.getArgB(), test.getArgC(), mockMvc);
                            TestUtil.HelperPage<User> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<User>>() {
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
    @Transactional
    void create() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        User userTest = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

        User admin = userRepository.findByEmail("admin@localhost").orElse(null);

        User createdUser = TestUtil.getDtoByIdByUser(admin, "admin", userTest, "/users/", mockMvc, User.class);

        User userTestCreateDtoRequest = (User) SerializationUtils.clone(createdUser);
        userTestCreateDtoRequest.setEmail("testCreate-email");
        userTestCreateDtoRequest.setId("");


        User responseDto = TestUtil.postDtoByUser(admin, "admin", userTestCreateDtoRequest, "/users/", mockMvc, User.class);

        //copy request and enrich with fields that backend should set itself
        User expectedResponseDTO = (User) SerializationUtils.clone(userTestCreateDtoRequest);
        expectedResponseDTO.setVersion(0);

        User userTestto = new User();
        userTestto.setId(admin.getId());

        expectedResponseDTO.setCreatedBy(userTestto);
        expectedResponseDTO.setLastUpdBy(userTestto);

        //copy responce fields we cannot predict
        expectedResponseDTO.setCreatedDate(responseDto.getCreatedDate());
        expectedResponseDTO.setUpdatedDate(responseDto.getUpdatedDate());

        assertAll(
                () -> assertThat(responseDto).isEqualToComparingFieldByField(expectedResponseDTO)
        );

    }

    @Test
    @Transactional
    void update() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        User userTest = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        User createdUser = TestUtil.getDtoByIdByUser(admin, "admin", userTest, "/users/", mockMvc, User.class);
        User userTestAdminUpdateDtoRequest = (User) SerializationUtils.clone(createdUser);
        userTestAdminUpdateDtoRequest.setEmail("testUpdate-email@localhost");
        TestUtil.putDtoByUser(admin, "admin", userTestAdminUpdateDtoRequest, "/users/", mockMvc, User.class);

        //успешно обновлена книга
        Iterable<User> createdUsers = userRepository.findAll(new BooleanBuilder().and(QUser.user.email.eq(userTestAdminUpdateDtoRequest.getEmail())));

        assertAll(
                () -> assertThat(createdUsers).hasSize(1)
        );

    }


    @Test
    @Transactional
    void delete() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        User userTest = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(admin.getEmail()).password("admin").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userTest.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        assertAll(
                () -> assertThat(userRepository.findById(userTest.getId())).isEmpty()
        );
    }
}
