package com.lig.libby.controller.adapter.userui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.userui.dto.UserPublicDto;
import com.lig.libby.core.TestUtil;
import com.lig.libby.core.TestUtil.TestArgs;
import com.lig.libby.domain.QUser;
import com.lig.libby.domain.User;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.AuthorityRepository;
import com.lig.libby.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
public class UserPublicControllerTest {

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

    public void findOneTest() throws Exception {
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, "admin@localhost", "admin");
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);


        UserPublicDto assertResponce = new UserPublicDto();
        assertResponce.setId(user.getId());
        assertResponce.setName(user.getName());
        assertResponce.setVersion(user.getVersion());

        UserPublicDto userPublicDto = new UserPublicDto();
        userPublicDto.setId(user.getCreatedBy().getId());

        UserPublicDto response = TestUtil.getDtoByIdByUser(user, "test", user, "/usersPublic/", mockMvc, UserPublicDto.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }



    @TestFactory
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    Stream<DynamicTest> dynamicFindAllTest() {
        List<TestArgs<User, String, String, List<User>, String, String, String>> inputList;
        {

            User admin = userRepository.findByEmail("admin@localhost").orElse(null);
            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, Objects.requireNonNull(admin).getEmail(), "admin");
            userRepository.deleteAll(userRepository.findAll(new BooleanBuilder().and(QUser.user.email.eq("admin@localhost")).not()));
            User user1 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
            User user2 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

            inputList = Arrays.asList(
                    //search all && role access
                    new TestArgs<>(admin, "admin", "/usersPublic/",
                            Arrays.asList(admin, user1, user2), null, null, null),
                    new TestArgs<>(user1, "test", "/usersPublic/",
                            Arrays.asList(admin, user1, user2), null, null, null),
                    new TestArgs<>(user2, "test", "/usersPublic/",
                            Arrays.asList(admin, user1, user2), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/usersPublic/?name=" + user1.getName(),
                            Arrays.asList(user1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/usersPublic/?name=" + user1.getName() + "&name=" + user2.getName(),
                            Arrays.asList(user1, user2), null, null, null)
            );
        }

        return inputList.stream()
                .map(test -> DynamicTest.dynamicTest(
                        " When call url: " + test.getArgC() +
                                " by user: " + test.getArgA().getEmail() +
                                " loggined with password: " + test.getArgB() +
                                " then see only users: " + test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.joining(",")),
                        () -> {

                            String responsePageJson = TestUtil.getDtoPageByQueryParamsByUser(test.getArgA(), test.getArgB(), test.getArgC(), mockMvc);
                            TestUtil.HelperPage<UserPublicDto> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<UserPublicDto>>() {
                            }).readValue(responsePageJson);

                            assertAll(
                                    () -> assertThat(responsePage.stream().map(PersistentObject::getId).collect(Collectors.toList())).
                                            containsExactlyInAnyOrderElementsOf(test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.toList()))
                            );
                        }
                        )
                );
    }
}
