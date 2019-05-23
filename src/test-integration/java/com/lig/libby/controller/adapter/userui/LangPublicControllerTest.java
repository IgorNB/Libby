package com.lig.libby.controller.adapter.userui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.userui.dto.LangPublicDto;
import com.lig.libby.controller.adapter.userui.dto.UserPublicDto;
import com.lig.libby.core.TestUtil;
import com.lig.libby.core.TestUtil.TestArgs;
import com.lig.libby.domain.Lang;
import com.lig.libby.domain.User;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.AuthorityRepository;
import com.lig.libby.repository.LangRepository;
import com.lig.libby.repository.UserRepository;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
public class LangPublicControllerTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public LangRepository langRepository;

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

        LangPublicDto assertResponce = new LangPublicDto();
        assertResponce.setId(lang.getId());
        assertResponce.setCode(lang.getCode());
        assertResponce.setVersion(lang.getVersion());

        UserPublicDto userPublicDto = new UserPublicDto();
        userPublicDto.setId(lang.getCreatedBy().getId());
        assertResponce.setCreatedBy(userPublicDto);
        assertResponce.setLastUpdBy(userPublicDto);

        LangPublicDto response = TestUtil.getDtoByIdByUser(user, "test", lang, "/langsPublic/", mockMvc, LangPublicDto.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }


    @TestFactory
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    Stream<DynamicTest> dynamicFindAllTest() {
        List<TestArgs<User, String, String, List<Lang>, String, String, String>> inputList;
        {

            User admin = userRepository.findByEmail("admin@localhost").orElse(null);
            User user1 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
            User user2 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
            langRepository.deleteAll();

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user1.getEmail(), "test");
            Lang lang1 = TestUtil.createAndSaveLang(langRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user2.getEmail(), "test");
            Lang lang2 = TestUtil.createAndSaveLang(langRepository);
            Lang lang3 = TestUtil.createAndSaveLang(langRepository);

            inputList = Arrays.asList(
                    //search all && role access
                    new TestArgs<>(user1, "test", "/langsPublic/",
                            Arrays.asList(lang1, lang2, lang3), null, null, null),
                    new TestArgs<>(user2, "test", "/langsPublic/",
                            Arrays.asList(lang1, lang2, lang3), null, null, null),
                    new TestArgs<>(admin, "admin", "/langsPublic/",
                            Arrays.asList(lang1, lang2, lang3), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/langsPublic/?createdBy.id=" + lang1.getCreatedBy().getId(),
                            Arrays.asList(lang1), null, null, null),
                    new TestArgs<>(user2, "test", "/langsPublic/?createdBy.id=" + lang2.getCreatedBy().getId(),
                            Arrays.asList(lang2, lang3), null, null, null),
                    new TestArgs<>(admin, "admin", "/langsPublic/?createdBy.id=" + lang3.getCreatedBy().getId(),
                            Arrays.asList(lang2, lang3), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/langsPublic/?code=" + lang1.getCode(),
                            Arrays.asList(lang1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/langsPublic/?id=" + lang1.getId() + "&id=" + lang2.getId(),
                            Arrays.asList(lang1, lang2), null, null, null)
            );
        }

        return inputList.stream()
                .map(test -> DynamicTest.dynamicTest(
                        " When call url: " + test.getArgC() +
                                " by user: " + test.getArgA().getEmail() +
                                " loggined with password: " + test.getArgB() +
                                " then see only langs: " + test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.joining(",")),
                        () -> {

                            String responsePageJson = TestUtil.getDtoPageByQueryParamsByUser(test.getArgA(), test.getArgB(), test.getArgC(), mockMvc);
                            TestUtil.HelperPage<LangPublicDto> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<LangPublicDto>>() {
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
