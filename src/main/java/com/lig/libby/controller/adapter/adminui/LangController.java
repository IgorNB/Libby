package com.lig.libby.controller.adapter.adminui;

import com.lig.libby.controller.adapter.adminui.mapper.read.LangUiApiReadMapper;
import com.lig.libby.controller.adapter.adminui.mapper.save.LangUiApiSaveMapper;
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

@RestController
@RequestMapping("/langs")
@SuppressWarnings("squid:S4684")
class LangController implements GenricUIApiController<Lang, String> {
    private final LangService service;
    private final LangUiApiReadMapper readMapper;
    private final LangUiApiSaveMapper saveMapper;

    @Autowired
    public LangController(LangService service, LangUiApiReadMapper readMapper, LangUiApiSaveMapper saveMapper) {
        this.readMapper = readMapper;
        this.saveMapper = saveMapper;
        this.service = service;
    }


    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping
    //Here we assume that Entity and DTO have:
    // 1) same field names (DTO can has only subset) due to @QuerydslPredicate is builded for Lang.class, while api request is made for LangDto.class
    // 2) Pagination for is not broken during Mapper work (e.g no filtering in mapping process used) due to pageable usage
    // 3) bindings = ... is needed only for @ActiveProfile eq. "springJdbc" and "springJpa" where Repository bean is custom, so does not support bind with repository itself
    public Page<Lang> findAll(@QuerydslPredicate(root = Lang.class) Predicate predicate, Pageable pageable, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.pageableEntityToDto(
                service.findAll(predicate, pageable, userDetails)
        );
    }

    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping(value = "/{id}")
    public Lang findOne(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.entityToDto(service.findById(id, userDetails));
    }

    @PostMapping
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    public Lang create(@RequestBody Lang dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Lang entity = saveMapper.dtoToCreateEntity(dto);
        return readMapper.entityToDto(service.create(entity, userDetails));
    }

    @PutMapping(value = "/{id}")
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public Lang update(@RequestBody Lang dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Lang entity = saveMapper.dtoToCreateEntity(dto);
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

