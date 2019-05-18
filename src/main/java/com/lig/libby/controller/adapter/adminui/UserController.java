package com.lig.libby.controller.adapter.adminui;

import com.lig.libby.controller.adapter.adminui.mapper.read.UserUiApiReadMapper;
import com.lig.libby.controller.adapter.adminui.mapper.save.UserUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.GenricUIApiController;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.User;
import com.lig.libby.service.UserService;
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
@RequestMapping("/users")
@SuppressWarnings("squid:S4684")
class UserController implements GenricUIApiController<User, String> {
    private final UserService service;
    private final UserUiApiReadMapper readMapper;
    private final UserUiApiSaveMapper saveMapper;

    @Autowired
    public UserController(UserService service, UserUiApiReadMapper readMapper, UserUiApiSaveMapper saveMapper) {
        this.readMapper = readMapper;
        this.saveMapper = saveMapper;
        this.service = service;
    }


    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping
    //Here we assume that Entity and DTO have:
    // 1) same field names (DTO can has only subset) due to @QuerydslPredicate is builded for User.class, while api request is made for UserDto.class
    // 2) Pagination for is not broken during Mapper work (e.g no filtering in mapping process used) due to pageable usage
    // 3) bindings = ... is needed only for @ActiveProfile eq. "springJdbc" and "springJpa" where Repository bean is custom, so does not support bind with repository itself
    public Page<User> findAll(@QuerydslPredicate(root = User.class) Predicate predicate, Pageable pageable, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.pageableEntityToDto(service.findAll(predicate, pageable, userDetails));
    }

    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping(value = "/{id}")
    public User findOne(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.entityToDto(service.findById(id, userDetails));
    }

    @PostMapping
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User entity = saveMapper.dtoToCreateEntity(dto);
        return readMapper.entityToDto(service.create(entity, userDetails));
    }

    @PutMapping(value = "/{id}")
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public User update(@RequestBody User dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User entity = saveMapper.dtoToCreateEntity(dto);
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

