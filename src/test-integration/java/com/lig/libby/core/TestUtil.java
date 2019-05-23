package com.lig.libby.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lig.libby.controller.adapter.anonymousui.AuthControllerTest;
import com.lig.libby.controller.adapter.anonymousui.dto.LoginRequestDto;
import com.lig.libby.domain.*;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.*;
import com.querydsl.core.BooleanBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class TestUtil {
    private TestUtil() {
    }

    @NotNull
    public static Comment createAndSaveComment(User user, Book book, CommentRepository commentRepository) {
        String taskName = "test-task-name" + UUID.randomUUID().toString().replaceAll("-", "");

        Comment comment = new Comment();
        comment.setBook(book);
        comment.setRating(Math.toIntExact(Math.round(Math.random() * 5)));
        comment.setBody("test-comment-body" + UUID.randomUUID().toString().replaceAll("-", ""));

        comment = commentRepository.saveAndFind(comment);
        return comment;
    }

    @NotNull
    public static Task createAndSaveTask(User user, Lang lang, Work work, TaskRepository taskRepository) {
        String taskName = "test-task-name" + UUID.randomUUID().toString().replaceAll("-", "");

        Task task = new Task();
        task.setBookName(taskName);
        task.setBookLang(lang);
        task.setBookWork(work);
        task.setBookTitle("test-title-name" + UUID.randomUUID().toString().replaceAll("-", ""));
        task.setWorkflowStep(Task.WorkflowStepEnum.INIT);
        task.setAssignee(user);
        task = taskRepository.saveAndFind(task);
        return task;
    }

    @NotNull
    public static Book createAndSaveBook(User user, Lang lang, Work work, BookRepository bookRepository) {
        String taskName = "test-task-name" + UUID.randomUUID().toString().replaceAll("-", "");
        Book book = new Book();
        book.setName(taskName);
        book.setLang(lang);
        book.setWork(work);
        book.setTitle("test-title-name" + UUID.randomUUID().toString().replaceAll("-", ""));
        book = bookRepository.saveAndFind(book);
        return book;
    }

    @NotNull
    public static Lang createAndSaveLang(LangRepository langRepository) {
        String langName = "test-lang-name" + UUID.randomUUID().toString().replaceAll("-", "");
        Lang langNew = new Lang();
        langNew.setCode(langName);
        return langRepository.saveAndFind(langNew);
    }

    @NotNull
    public static User createAndSaveUserWithUserRole(PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, UserRepository userRepository) {
        User user = new User();
        user.setName("user-jwt-controller" + UUID.randomUUID().toString().replaceAll("-", ""));
        user.setEmail("user-jwt-controller" + UUID.randomUUID().toString().replaceAll("-", "") + "@example.com");
        user.setEmailVerified(true);
        user.setProvider(Authority.AuthProvider.local);
        user.setPassword(passwordEncoder.encode("test"));
        BooleanBuilder where = new BooleanBuilder().and(QAuthority.authority.name.eq(Authority.Roles.USER));
        Authority authority = authorityRepository.findAll(where).iterator().next();
        user.setAuthorities(new HashSet<>(Arrays.asList(authority)));

        return userRepository.saveAndFind(user);
    }

    @NotNull
    public static User createAndSaveUserWithAdminRole(PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, UserRepository userRepository) {
        User user = new User();
        user.setName("user-jwt-controller" + UUID.randomUUID().toString().replaceAll("-", ""));
        user.setEmail("user-jwt-controller" + UUID.randomUUID().toString().replaceAll("-", "") + "@example.com");
        user.setEmailVerified(true);
        user.setProvider(Authority.AuthProvider.local);
        user.setPassword(passwordEncoder.encode("test"));
        BooleanBuilder where = new BooleanBuilder().and(QAuthority.authority.name.eq(Authority.Roles.ADMIN));
        Authority authority = authorityRepository.findAll(where).iterator().next();
        user.setAuthorities(new HashSet<>(Arrays.asList(authority)));

        return userRepository.saveAndFind(user);
    }

    @NotNull
    public static Work createAndSaveWork(WorkRepository workRepository) {
        return workRepository.saveAndFind(new Work());
    }

    public static void setAuthenticationForCurrentThreadLocal(AuthenticationManager authenticationManager, String login, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static <D extends PersistentObject, E extends PersistentObject> D getDtoByIdByUser(User user, String password, E entity, String url, MockMvc mockMvc, Class<D> clazz) throws Exception {
        D createdTaskDto;
        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password(password).build());
        mockMvc.perform(MockMvcRequestBuilders.get(url + entity.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        createdTaskDto = new ObjectMapper().readValue(responceBodyRef.get(), clazz);
        return createdTaskDto;
    }

    public static String getDtoPageByQueryParamsByUser(User user, String password, String urlWithQueryParams, MockMvc mockMvc) throws Exception {

        AtomicReference<String> responceBodyRef = new AtomicReference<>();
        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password(password).build());
        mockMvc.perform(MockMvcRequestBuilders.get(Optional.ofNullable(urlWithQueryParams).orElse(""))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        return responceBodyRef.get();

    }

    public static <D extends PersistentObject> D putDtoByUser(User user, String password, D entityDto, String url, MockMvc mockMvc, Class<D> clazz) throws Exception {
        AtomicReference<String> responceBodyRef = new AtomicReference<>();

        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password(password).build());
        mockMvc.perform(MockMvcRequestBuilders.put(url + entityDto.getId())
                .header("Authorization", "Bearer " + token)
                //.accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(entityDto))
                .characterEncoding("utf-8")
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        return new ObjectMapper().readValue(responceBodyRef.get(), clazz);
    }

    public static <D extends PersistentObject> D postDtoByUser(User user, String password, D entityDto, String url, MockMvc mockMvc, Class<D> clazz) throws Exception {
        AtomicReference<String> responceBodyRef = new AtomicReference<>();

        String token = AuthControllerTest.authUser(mockMvc, LoginRequestDto.builder().email(user.getEmail()).password(password).build());
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("Authorization", "Bearer " + token)
                //.accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(entityDto))
                .characterEncoding("utf-8")
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(mvcResult -> responceBodyRef.set(mvcResult.getResponse().getContentAsString()));

        return new ObjectMapper().readValue(responceBodyRef.get(), clazz);
    }


    @JsonIgnoreProperties(value = {"pageable"}, allowGetters = true, ignoreUnknown = true)
    public static class HelperPage<T> extends PageImpl<T> {

        @JsonCreator
        // Note: I don't need a sort, so I'm not including one here.
        // It shouldn't be too hard to add it in tho.
        public HelperPage(@JsonProperty("content") List<T> content,
                          @JsonProperty("number") int number,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") Long totalElements
        ) {
            super(content, PageRequest.of(number, size), totalElements);
            //System.out.println(content);
        }
    }

    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    public static class TestArgs<A, B, C, D, E, F, G> {
        private final A argA;
        private final B argB;
        private final C argC;
        private final D argD;
        private final E argE;
        private final F argF;
        private final G argG;
    }
}
