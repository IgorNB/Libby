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
public class CommentControllerTest {

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
    private BookRepository bookRepository;

    @Autowired
    public CommentRepository commentRepository;

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
        User user = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);
        Comment comment = TestUtil.createAndSaveComment(user, book, commentRepository);

        Comment assertResponce = new Comment();
        assertResponce.setId(comment.getId());

        assertResponce.setBody(comment.getBody());

        Book bookDto = new Book();
        bookDto.setId(comment.getBook().getId());
        assertResponce.setBook(bookDto);
        assertResponce.setRating(comment.getRating());

        assertResponce.setVersion(comment.getVersion());

        User userDto = new User();
        userDto.setId(comment.getCreatedBy().getId());
        assertResponce.setCreatedBy(userDto);
        assertResponce.setLastUpdBy(userDto);


        Comment response = TestUtil.getDtoByIdByUser(user, "test", comment, "/comments/", mockMvc, Comment.class);

        assertAll(
                () -> assertThat(assertResponce).isEqualToComparingFieldByField(response)
        );
    }


    @TestFactory
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    Stream<DynamicTest> dynamicFindAllTest() {
        List<TestArgs<User, String, String, List<Comment>, String, String, String>> inputList;
        {
            User admin = userRepository.findByEmail("admin@localhost").orElse(null);
            User user1 = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);
            User user2 = TestUtil.createAndSaveUserWithAdminRole(passwordEncoder, authorityRepository, userRepository);

            Lang lang = TestUtil.createAndSaveLang(langRepository);
            Work work = TestUtil.createAndSaveWork(workRepository);
            Book book12 = TestUtil.createAndSaveBook(user1, lang, work, bookRepository);
            Book book3 = TestUtil.createAndSaveBook(user1, TestUtil.createAndSaveLang(langRepository), work, bookRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user1.getEmail(), "test");
            Comment comment1 = TestUtil.createAndSaveComment(user1, book12, commentRepository);

            TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user2.getEmail(), "test");
            Comment comment2 = TestUtil.createAndSaveComment(user2, book12, commentRepository);
            Comment comment3 = TestUtil.createAndSaveComment(user2, book3, commentRepository);

            inputList = Arrays.asList(
                    //search all && role access
                    new TestArgs<>(user1, "test", "/comments/",
                            Arrays.asList(comment1, comment2, comment3), null, null, null),
                    new TestArgs<>(user2, "test", "/comments/",
                            Arrays.asList(comment1, comment2, comment3), null, null, null),
                    new TestArgs<>(admin, "admin", "/comments/",
                            Arrays.asList(comment1, comment2, comment3), null, null, null),

                    //search by child Entity field & role access
                    new TestArgs<>(user1, "test", "/comments/?book.id=" + book12.getId(),
                            Arrays.asList(comment1, comment2), null, null, null),
                    new TestArgs<>(user1, "test", "/comments/?book.id=" + book3.getId(),
                            Arrays.asList(comment3), null, null, null),
                    new TestArgs<>(admin, "admin", "/comments/?book.id=" + book12.getId(),
                            Arrays.asList(comment1, comment2), null, null, null),
                    new TestArgs<>(admin, "admin", "/comments/?book.id=" + book3.getId(),
                            Arrays.asList(comment3), null, null, null),

                    //search by value field
                    new TestArgs<>(admin, "admin", "/comments/?body=" + comment1.getBody(),
                            Arrays.asList(comment1), null, null, null),

                    //search by value field with OR
                    new TestArgs<>(admin, "admin", "/comments/?id=" + comment1.getId() + "&id=" + comment2.getId(),
                            Arrays.asList(comment1, comment2), null, null, null)
            );
        }

        return inputList.stream()
                .map(test -> DynamicTest.dynamicTest(
                        " When call url: " + test.getArgC() +
                                " by user: " + test.getArgA().getEmail() +
                                " loggined with password: " + test.getArgB() +
                                " then see only comments: " + test.getArgD().stream().map(PersistentObject::getId).collect(Collectors.joining(",")),
                        () -> {
                            String responsePageJson = TestUtil.getDtoPageByQueryParamsByUser(test.getArgA(), test.getArgB(), test.getArgC(), mockMvc);
                            TestUtil.HelperPage<Comment> responsePage = new ObjectMapper().readerFor(new TypeReference<TestUtil.HelperPage<Comment>>() {
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
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);
        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        Comment comment = TestUtil.createAndSaveComment(user, book, commentRepository);


        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        Comment findResponseDto = TestUtil.getDtoByIdByUser(admin, "admin", comment, "/comments/", mockMvc, Comment.class);
        Comment createDto = (Comment) SerializationUtils.clone(findResponseDto);
        createDto.setId(new Comment().getId());
        createDto.setVersion(null);

        Comment responseDto = TestUtil.postDtoByUser(admin, "admin", createDto, "/comments/", mockMvc, Comment.class);

        //copy request and enrich with fields that backend should set itself
        Comment expectedResponseDTO = (Comment) SerializationUtils.clone(createDto);

        expectedResponseDTO.setVersion(0);

        User userDto = new User();
        userDto.setId(admin.getId());

        expectedResponseDTO.setCreatedBy(userDto);
        expectedResponseDTO.setLastUpdBy(userDto);

        //copy responce fields we cannot predict
        expectedResponseDTO.setId(responseDto.getId());
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
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);


        TestUtil.setAuthenticationForCurrentThreadLocal(authenticationManager, user.getEmail(), "test");
        Comment comment = TestUtil.createAndSaveComment(user, book, commentRepository);

        Comment createdComment = TestUtil.getDtoByIdByUser(admin, "admin", comment, "/comments/", mockMvc, Comment.class);
        Comment commentAdminUpdateDtoRequest = (Comment) SerializationUtils.clone(createdComment);
        commentAdminUpdateDtoRequest.setBody("testUpdate-body");
        TestUtil.putDtoByUser(admin, "admin", commentAdminUpdateDtoRequest, "/comments/", mockMvc, Comment.class);

        //успешно обновлена книга
        Iterable<Comment> createdComments = commentRepository.findAll(new BooleanBuilder().and(QComment.comment.body.eq(commentAdminUpdateDtoRequest.getBody())));

        assertAll(
                () -> assertThat(createdComments).hasSize(1)
        );

    }


    @Test
    void delete() throws Exception {

        User user = TestUtil.createAndSaveUserWithUserRole(passwordEncoder, authorityRepository, userRepository);
        Lang lang = TestUtil.createAndSaveLang(langRepository);
        Work work = TestUtil.createAndSaveWork(workRepository);
        Book book = TestUtil.createAndSaveBook(user, lang, work, bookRepository);
        Comment comment = TestUtil.createAndSaveComment(user, book, commentRepository);


        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        User admin = userRepository.findByEmail("admin@localhost").orElse(null);
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(admin.getEmail()).password("admin").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/" + comment.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        assertAll(
                () -> assertThat(commentRepository.findById(comment.getId())).isEmpty()
        );
    }
}
