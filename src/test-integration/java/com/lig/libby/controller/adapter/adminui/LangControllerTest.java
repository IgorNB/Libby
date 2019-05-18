package com.lig.libby.controller.adapter.adminui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.anonymousui.AuthControllerTest;
import com.lig.libby.controller.adapter.anonymousui.dto.LoginRequestDto;
import com.lig.libby.core.TestUtil;
import com.lig.libby.core.TestUtil.TestArgs;
import com.lig.libby.domain.Lang;
import com.lig.libby.domain.QLang;
import com.lig.libby.domain.User;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.AuthorityRepository;
import com.lig.libby.repository.LangRepository;
import com.lig.libby.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.Getter;
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
public class LangControllerTest {

    @Autowired
    public AuthenticationManager authenticationManager;

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public LangRepository langRepository;

    public MockMvc mockMvc;

    @Autowired
    public WebApplicationContext context;

    @Autowired
    public javax.servlet.Filter springSecurityFilterChain;


    @BeforeEach
    public void setup() {
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, "admin@localhost", "admin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
    }

    @Test
    @Transactional
    public void findOneTest() throws Exception {
        User user = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);

        Lang assertResponce = new Lang();
        assertResponce.setId(lang.getId());
        assertResponce.setCode(lang.getCode());
        assertResponce.setVersion(lang.getVersion());

        User userDto = new User();
        userDto.setId(lang.getCreatedBy().getId());
        assertResponce.setCreatedBy(userDto);
        assertResponce.setLastUpdBy(userDto);

        Lang response = TestUtil.getDtoByIdByUser(user, "test", lang, "/langs/", mockMvc, Lang.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }


    @Transactional
    @TestFactory
    Stream<DynamicTest> dynamicFindAllTest() {
        List<TestArgs<User, String, String, List<Lang>, String, String, String>> inputList;
        {

            User admin = userRepository.findByEmail("admin@localhost").orElse(null);
            User user1 = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
            User user2 = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
            langRepository.deleteAll();

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user1.getEmail(), "test");
            Lang lang1 = TestUtil.createAndSaveLang(langRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user2.getEmail(), "test");
            Lang lang2 = TestUtil.createAndSaveLang(langRepository);
            Lang lang3 = TestUtil.createAndSaveLang(langRepository);

            inputList = Arrays.asList(
                    //search all && role access
                    new TestArgs<>(user1, "test", "/langs/",
                            Arrays.asList(lang1, lang2, lang3), null, null, null),
                    new TestArgs<>(user2, "test", "/langs/",
                            Arrays.asList(lang1, lang2, lang3), null, null, null),
                    new TestArgs<>(admin, "admin", "/langs/",
                            Arrays.asList(lang1, lang2, lang3), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/langs/?createdBy.id=" + lang1.getCreatedBy().getId(),
                            Arrays.asList(lang1), null, null, null),
                    new TestArgs<>(user2, "test", "/langs/?createdBy.id=" + lang2.getCreatedBy().getId(),
                            Arrays.asList(lang2, lang3), null, null, null),
                    new TestArgs<>(admin, "admin", "/langs/?createdBy.id=" + lang3.getCreatedBy().getId(),
                            Arrays.asList(lang2, lang3), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/langs/?code=" + lang1.getCode(),
                            Arrays.asList(lang1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/langs/?id=" + lang1.getId() + "&id=" + lang2.getId(),
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
                            TestUtil.HelperPage<Lang> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<Lang>>() {
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
        Lang lang = TestUtil.createAndSaveLang(langRepository);

        Lang requestDto = new Lang();

        Lang langDto = new Lang();
        langDto.setId(lang.getId());

        requestDto.setId("test-lang-" + UUID.randomUUID().toString().replaceAll("-", ""));
        requestDto.setCode("lang-name-" + requestDto.getId());


        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        Lang responseDto = TestUtil.postDtoByUser(admin, "admin", requestDto, "/langs/", mockMvc, Lang.class);

        //copy request and enrich with fields that backend should set itself
        Lang expectedResponseDTO = (Lang) SerializationUtils.clone(requestDto);
        expectedResponseDTO.setVersion(0);

        User userDto = new User();
        userDto.setId(admin.getId());

        expectedResponseDTO.setCreatedBy(userDto);
        expectedResponseDTO.setLastUpdBy(userDto);

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
        Lang lang = TestUtil.createAndSaveLang(langRepository);

        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        Lang createdLang = TestUtil.getDtoByIdByUser(admin, "admin", lang, "/langs/", mockMvc, Lang.class);
        Lang langAdminUpdateDtoRequest = (Lang) SerializationUtils.clone(createdLang);
        langAdminUpdateDtoRequest.setCode("testUpdate-code");
        TestUtil.putDtoByUser(admin, "admin", langAdminUpdateDtoRequest, "/langs/", mockMvc, Lang.class);

        //успешно обновлена книга
        Iterable<Lang> createdLangs = langRepository.findAll(new BooleanBuilder().and(QLang.lang.code.eq(langAdminUpdateDtoRequest.getCode())));

        assertAll(
                () -> assertThat(createdLangs).hasSize(1)
        );

    }


    @Test
    @Transactional
    void delete() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);

        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(admin.getEmail()).password("admin").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/langs/" + lang.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        assertAll(
                () -> assertThat(langRepository.findById(lang.getId())).isEmpty()
        );
    }
}
