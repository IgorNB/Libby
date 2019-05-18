package com.lig.libby.controller.adapter.userui;

import com.lig.libby.controller.adapter.userui.dto.TaskPublicDto;
import com.lig.libby.controller.adapter.userui.mapper.read.TaskPublicUiApiReadMapper;
import com.lig.libby.controller.adapter.userui.mapper.save.TaskPublicUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.GenricUIApiController;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.Task;
import com.lig.libby.repository.TaskRepository;
import com.lig.libby.service.TaskService;
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
@RequestMapping("/tasksPublic")
public class TaskPublicController implements GenricUIApiController<TaskPublicDto, String> {
    private final TaskService service;

    private final TaskPublicUiApiReadMapper readMapper;

    private final TaskPublicUiApiSaveMapper saveMapper;


    @Autowired
    public TaskPublicController(TaskService service, TaskPublicUiApiReadMapper readMapper, TaskPublicUiApiSaveMapper saveMapper) {
        this.readMapper = readMapper;
        this.saveMapper = saveMapper;
        this.service = service;
    }


    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @GetMapping
    //Here we assume that Entity and DTO have:
    // 1) same field names (DTO can has only subset) due to @QuerydslPredicate is builded for Task.class, while api request is made for TaskDto.class
    // 2) Pagination for is not broken during Mapper work (e.g no filtering in mapping process used) due to pageable usage
    // 3) bindings = ... is needed only for @ActiveProfile eq. "springJdbc" and "springJpa" where Repository bean is custom, so does not support bind with repository itself
    public Page<TaskPublicDto> findAll(@QuerydslPredicate(root = Task.class, bindings = TaskRepository.class) Predicate predicate, Pageable pageable, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.pageableEntityToDto(service.findAll(predicate, pageable, userDetails));
    }

    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @GetMapping(value = "/{id}")
    public TaskPublicDto findOne(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.entityToDto(service.findById(id, userDetails));
    }

    @PostMapping
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.CREATED)
    public TaskPublicDto create(@RequestBody TaskPublicDto dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Task entity = saveMapper.dtoToCreateEntity(dto);
        return readMapper.entityToDto(service.create(entity, userDetails));
    }

    @PutMapping(value = "/{id}")
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.OK)
    public TaskPublicDto update(@RequestBody TaskPublicDto dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Task entity = saveMapper.dtoToCreateEntity(dto);
        return readMapper.entityToDto(service.update(entity, userDetails));
    }

    @DeleteMapping(value = "/{id}")
    @RolesAllowed({Authority.Roles.USER, Authority.Roles.ADMIN})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        service.deleteById(service.findById(id, userDetails).getId());
    }

}

