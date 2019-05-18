package com.lig.libby.controller.adapter.userui;

import com.lig.libby.controller.adapter.userui.dto.LangPublicDto;
import com.lig.libby.controller.adapter.userui.mapper.read.LangPublicUiApiReadMapper;
import com.lig.libby.controller.core.uiadapter.GenricUIApiController;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.Lang;
import com.lig.libby.service.LangService;
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
@RequestMapping("/langsPublic")
public class LangPublicController implements GenricUIApiController<LangPublicDto, String> {
    private final LangService service;
    private final LangPublicUiApiReadMapper readMapper;

    @Autowired
    public LangPublicController(LangService service, LangPublicUiApiReadMapper readMapper) {
        this.readMapper = readMapper;
        this.service = service;
    }


    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @GetMapping
    //Here we assume that Entity and DTO have:
    // 1) same field names (DTO can has only subset) due to @QuerydslPredicate is builded for Lang.class, while api request is made for LangDto.class
    // 2) Pagination for is not broken during Mapper work (e.g no filtering in mapping process used) due to pageable usage
    // 3) bindings = ... is needed only for @ActiveProfile eq. "springJdbc" and "springJpa" where Repository bean is custom, so does not support bind with repository itself
    public Page<LangPublicDto> findAll(@QuerydslPredicate(root = Lang.class) Predicate predicate, Pageable pageable, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.pageableEntityToDto(service.findAll(predicate, pageable, userDetails));
    }

    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @GetMapping(value = "/{id}")
    public LangPublicDto findOne(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.entityToDto(service.findById(id, userDetails));
    }

    @PostMapping
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.CREATED)
    public LangPublicDto create(@RequestBody LangPublicDto dto, Authentication authentication) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @PutMapping(value = "/{id}")
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.OK)
    public LangPublicDto update(@RequestBody LangPublicDto dto, Authentication authentication) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @DeleteMapping(value = "/{id}")
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id, Authentication authentication) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

}

