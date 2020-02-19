package com.lig.libby.controller.adapter.userui;

import com.lig.libby.controller.adapter.userui.dto.UserPublicDto;
import com.lig.libby.controller.adapter.userui.mapper.read.UserPublicUiApiReadMapper;
import com.lig.libby.controller.core.uiadapter.GenricUIApiController;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.User;
import com.lig.libby.repository.UserRepository;
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
import javax.naming.OperationNotSupportedException;

@RestController
@RequestMapping("/usersPublic")
public class UserPublicController implements GenricUIApiController<UserPublicDto, String> {
    private final UserService service;
    private final UserPublicUiApiReadMapper readMapper;

    @Autowired
    public UserPublicController(UserService service, UserPublicUiApiReadMapper readMapper) {
        this.readMapper = readMapper;
        this.service = service;
    }


    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @GetMapping
    //Here we assume that Entity and DTO have:
    // 1) same field names (DTO can has only subset) due to @QuerydslPredicate is builded for User.class, while api request is made for UserDto.class
    // 2) Pagination for is not broken during Mapper work (e.g no filtering in mapping process used) due to pageable usage
    // 3) bindings = ... is needed only for @ActiveProfile eq. "springJdbc" and "springJpa" where Repository bean is custom, so does not support bind with repository itself
    public Page<UserPublicDto> findAll(@QuerydslPredicate(root = User.class, bindings = UserRepository.class) Predicate predicate, Pageable pageable, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.pageableEntityToDto(service.findAll(predicate, pageable, userDetails));
    }

    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @GetMapping(value = "/{id}")
    public UserPublicDto findOne(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.entityToDto(service.findById(id, userDetails));
    }

    @PostMapping
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.CREATED)
    public UserPublicDto create(@RequestBody UserPublicDto dto, Authentication authentication) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @PutMapping(value = "/{id}")
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.OK)
    public UserPublicDto update(@RequestBody UserPublicDto dto, Authentication authentication) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @DeleteMapping(value = "/{id}")
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id, Authentication authentication) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

}

