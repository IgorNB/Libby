package com.lig.libby.controller.adapter.adminui;

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

public class BookControllerTest {

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
    public BookRepository bookRepository;

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
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);

        Book assertResponce = new Book();
        assertResponce.setId(book.getId());

        assertResponce.setName(book.getName());

        Lang langDto = new Lang();
        langDto.setId(book.getLang().getId());
        assertResponce.setLang(langDto);

        Work workDto = new Work();
        workDto.setId(book.getWork().getId());
        assertResponce.setWork(workDto);

        assertResponce.setTitle(book.getTitle());

        assertResponce.setVersion(book.getVersion());

        User userDto = new User();
        userDto.setId(book.getCreatedBy().getId());
        assertResponce.setCreatedBy(userDto);
        assertResponce.setLastUpdBy(userDto);

        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        Book response = TestUtil.getDtoByIdByUser(admin, "admin", book, "/books/", mockMvc, Book.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }


    @TestFactory
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    Stream<DynamicTest> dynamicFindAllTest() {
        List<TestArgs<User, String, String, List<Book>, String, String, String>> inputList;
        {
            User admin = userRepository.findByEmail("admin@localhost").orElse(null);
            User user1 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
            User user2 = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);

            Lang book12Lang = TestUtil.createAndSaveLang(langRepository);
            Work book13Work = TestUtil.createAndSaveWork(workRepository);
            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user1.getEmail(), "test");
            Book book1 = TestUtil.createAndSaveBook(user1, book12Lang, book13Work, bookRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user2.getEmail(), "test");
            Book book2 = TestUtil.createAndSaveBook(user2, book12Lang, TestUtil.createAndSaveWork(workRepository), bookRepository);
            Book book3 = TestUtil.createAndSaveBook(user2, TestUtil.createAndSaveLang(langRepository), book13Work, bookRepository);

            inputList = Arrays.asList(
                    //search all && role access
                    new TestArgs<>(admin, "admin", "/books/",
                            Arrays.asList(book1, book2, book3), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(admin, "admin", "/books/?lang.id=" + book12Lang.getId(),
                            Arrays.asList(book1, book2), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(admin, "admin", "/books/?work.id=" + book13Work.getId(),
                            Arrays.asList(book1, book3), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/books/?id=" + book1.getId(),
                            Arrays.asList(book1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/books/?id=" + book1.getId() + "&id=" + book2.getId(),
                            Arrays.asList(book1, book2), null, null, null)
            );
        }

        return inputList.stream()
                .map(test -> DynamicTest.dynamicTest(
                        " When call url: " + test.getArgC() +
                                " by user: " + test.getArgA().getEmail() +
                                " loggined with password: " + test.getArgB() +
                                " then see only books: " + test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.joining(",")),
                        () -> {
                            String responsePageJson = TestUtil.getDtoPageByQueryParamsByUser(test.getArgA(), test.getArgB(), test.getArgC(), mockMvc);
                            TestUtil.HelperPage<Book> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<Book>>() {
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

        Book requestDto = new Book();

        Lang langDto = new Lang();
        langDto.setId(lang.getId());
        requestDto.setLang(langDto);

        Work workDto = new Work();
        workDto.setId(work.getId());
        requestDto.setWork(workDto);

        requestDto.setId("test-book-" + UUID.randomUUID().toString().replaceAll("-", ""));
        requestDto.setName("book-name-" + requestDto.getId());
        requestDto.setTitle("book-title-" + requestDto.getId());

        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        Book responseDto = TestUtil.postDtoByUser(admin, "admin", requestDto, "/books/", mockMvc, Book.class);

        //copy request and enrich with fields that backend should set itself
        Book expectedResponseDTO = (Book) SerializationUtils.clone(requestDto);
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
    void update() throws Exception {

        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);

        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);

        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        Book createdBook = TestUtil.getDtoByIdByUser(admin, "admin", book, "/books/", mockMvc, Book.class);
        Book bookAdminUpdateDtoRequest = (Book) SerializationUtils.clone(createdBook);
        bookAdminUpdateDtoRequest.setTitle("testUpdate-title");
        TestUtil.putDtoByUser(admin, "admin", bookAdminUpdateDtoRequest, "/books/", mockMvc, Book.class);

        //успешно обновлена книга
        Iterable<Book> createdBooks = bookRepository.findAll(new BooleanBuilder().and(QBook.book.title.eq(bookAdminUpdateDtoRequest.getTitle())));

        assertAll(
                () -> assertThat(createdBooks).hasSize(1)
        );

    }


    @Test
    void delete() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);


        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(admin.getEmail()).password("admin").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + book.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        assertAll(
                () -> assertThat(bookRepository.findById(book.getId())).isEmpty()
        );
    }
}
