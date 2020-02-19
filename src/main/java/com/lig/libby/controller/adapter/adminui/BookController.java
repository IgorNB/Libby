package com.lig.libby.controller.adapter.adminui;

import com.lig.libby.controller.adapter.adminui.mapper.read.BookUiApiReadMapper;
import com.lig.libby.controller.adapter.adminui.mapper.save.BookUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.GenricUIApiController;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.Book;
import com.lig.libby.repository.BookRepository;
import com.lig.libby.service.BookService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/books")
@SuppressWarnings("squid:S4684")
class BookController implements GenricUIApiController<Book, String> {
    private final BookService service;
    private final BookUiApiReadMapper readMapper;
    private final BookUiApiSaveMapper saveMapper;

    @Autowired
    public BookController(BookService service, BookUiApiReadMapper readMapper, BookUiApiSaveMapper saveMapper) {
        this.readMapper = readMapper;
        this.saveMapper = saveMapper;
        this.service = service;
    }


    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping
    //Here we assume that Entity and DTO have:
    // 1) same field names (DTO can has only subset) due to @QuerydslPredicate is builded for Book.class, while api request is made for BookDto.class
    // 2) Pagination for is not broken during Mapper work (e.g no filtering in mapping process used) due to pageable usage
    // 3) bindings = ... is needed only for @ActiveProfile eq. "springJdbc" and "springJpa" where Repository bean is custom, so does not support bind with repository itself
    public Page<Book> findAll(@QuerydslPredicate(root = Book.class, bindings = BookRepository.class) Predicate predicate, Pageable pageable, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.pageableEntityToDto(service.findAll(predicate, pageable, userDetails));
    }

    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping(value = "/{id}")
    public Book findOne(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.entityToDto(service.findById(id, userDetails));
    }

    @PostMapping
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Book entity = saveMapper.dtoToCreateEntity(dto);
        return readMapper.entityToDto(service.create(entity, userDetails));
    }

    @PutMapping(value = "/{id}")
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public Book update(@RequestBody Book dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Book entity = saveMapper.dtoToCreateEntity(dto);
        return readMapper.entityToDto(service.update(entity, userDetails));
    }

    @DeleteMapping(value = "/{id}")
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        service.deleteById(service.findById(id, userDetails).getId());
    }

}

