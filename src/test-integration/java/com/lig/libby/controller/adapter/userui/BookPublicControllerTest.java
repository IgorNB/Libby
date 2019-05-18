package com.lig.libby.controller.adapter.userui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.Main;
import com.lig.libby.controller.adapter.userui.dto.BookPublicDto;
import com.lig.libby.controller.adapter.userui.dto.LangPublicDto;
import com.lig.libby.controller.adapter.userui.dto.UserPublicDto;
import com.lig.libby.controller.adapter.userui.dto.WorkPublicDto;
import com.lig.libby.core.TestUtil;
import com.lig.libby.core.TestUtil.TestArgs;
import com.lig.libby.domain.Book;
import com.lig.libby.domain.Lang;
import com.lig.libby.domain.User;
import com.lig.libby.domain.Work;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
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
public class BookPublicControllerTest {

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
    @Transactional
    public void findOneTest() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);

        BookPublicDto assertResponce = new BookPublicDto();
        assertResponce.setId(book.getId());

        assertResponce.setName(book.getName());

        LangPublicDto langPublicDto = new LangPublicDto();
        langPublicDto.setId(book.getLang().getId());
        assertResponce.setLang(langPublicDto);

        WorkPublicDto workPublicDto = new WorkPublicDto();
        workPublicDto.setId(book.getWork().getId());
        assertResponce.setWork(workPublicDto);

        assertResponce.setTitle(book.getTitle());

        assertResponce.setVersion(book.getVersion());

        UserPublicDto userPublicDto = new UserPublicDto();
        userPublicDto.setId(book.getCreatedBy().getId());
        assertResponce.setCreatedBy(userPublicDto);
        assertResponce.setLastUpdBy(userPublicDto);


        BookPublicDto response = TestUtil.getDtoByIdByUser(user, "test", book, "/booksPublic/", mockMvc, BookPublicDto.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }


    @Transactional
    @TestFactory
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
                    new TestArgs<>(user1, "test", "/booksPublic/",
                            Arrays.asList(book1, book2, book3), null, null, null),
                    new TestArgs<>(user2, "test", "/booksPublic/",
                            Arrays.asList(book1, book2, book3), null, null, null),
                    new TestArgs<>(admin, "admin", "/booksPublic/",
                            Arrays.asList(book1, book2, book3), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/booksPublic/?lang.id=" + book12Lang.getId(),
                            Arrays.asList(book1, book2), null, null, null),
                    new TestArgs<>(user2, "test", "/booksPublic/?lang.id=" + book12Lang.getId(),
                            Arrays.asList(book1, book2), null, null, null),
                    new TestArgs<>(admin, "admin", "/booksPublic/?lang.id=" + book12Lang.getId(),
                            Arrays.asList(book1, book2), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/booksPublic/?work.id=" + book13Work.getId(),
                            Arrays.asList(book1, book3), null, null, null),
                    new TestArgs<>(user2, "test", "/booksPublic/?work.id=" + book13Work.getId(),
                            Arrays.asList(book1, book3), null, null, null),
                    new TestArgs<>(admin, "admin", "/booksPublic/?work.id=" + book13Work.getId(),
                            Arrays.asList(book1, book3), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/booksPublic/?id=" + book1.getId(),
                            Arrays.asList(book1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/booksPublic/?id=" + book1.getId() + "&id=" + book2.getId(),
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
                            TestUtil.HelperPage<BookPublicDto> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<BookPublicDto>>() {
                            }).readValue(responsePageJson);
                            assertAll(
                                    () -> assertThat(responsePage.stream().map(PersistentObject::getId).collect(Collectors.toList())).
                                            containsExactlyInAnyOrderElementsOf(test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.toList()))
                            );
                        }
                        )
                );
    }


    /*@Test
    @Transactional
    void create() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);

        BookPublicDto requestDto = new BookPublicDto();

        LangPublicDto langPublicDto = new LangPublicDto();
        langPublicDto.setId(lang.getId());
        requestDto.setLang(langPublicDto);

        WorkPublicDto workPublicDto = new WorkPublicDto();
        workPublicDto.setId(work.getId());
        requestDto.setWork(workPublicDto);

        requestDto.setId("test-book-" + UUID.randomUUID().toString().replaceAll("-", ""));
        requestDto.setName("book-name-" + requestDto.getId());
        requestDto.setTitle("book-title-" + requestDto.getId());

        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password("test").build());

        BookPublicDto responseDto = TestUtil.postDtoByUser(user, "test", requestDto, "/booksPublic/", mockMvc, BookPublicDto.class);

        //copy request and enrich with fields that backend should set itself
        BookPublicDto expectedResponseDTO = (BookPublicDto) SerializationUtils.clone(requestDto);
        expectedResponseDTO.setVersion(0);

        UserPublicDto userPublicDto = new UserPublicDto();
        userPublicDto.setId(user.getId());
       
        expectedResponseDTO.setCreatedBy(userPublicDto);
        expectedResponseDTO.setLastUpdBy(userPublicDto);

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
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);
        
        BookPublicDto createdBookDto = TestUtil.getDtoByIdByUser(user, "test", book, "/booksPublic/", mockMvc, BookPublicDto.class);
        BookPublicDto bookAdminUpdateDtoRequest = (BookPublicDto) SerializationUtils.clone(createdBookDto);
        bookAdminUpdateDtoRequest.setTitle("testUpdate-title");
        TestUtil.putDtoByUser(user, "test", bookAdminUpdateDtoRequest, "/booksPublic/", mockMvc, BookPublicDto.class);
        
        //успешно обновлена книга
        Iterable<Book> createdBooks = bookRepository.findAll(new BooleanBuilder().and(QBook.book.title.eq(bookAdminUpdateDtoRequest.getTitle())));

        assertAll(
                () -> assertThat(createdBooks).hasSize(1)
        );

    }


    @Test
    @Transactional
    void delete() throws Exception {
        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);

        Gson gson = new Gson();
        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password("test").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/booksPublic/" + book.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        assertAll(
                () -> assertThat(bookRepository.findById(book.getId())).isEmpty()
        );
    }*/
}
