package com.lig.libby.controller.adapter.adminui;

import com.lig.libby.controller.adapter.adminui.mapper.read.TaskUiApiReadMapper;
import com.lig.libby.controller.adapter.adminui.mapper.save.TaskUiApiSaveMapper;
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
@RequestMapping("/tasks")
@SuppressWarnings("squid:S4684")
class TaskController implements GenricUIApiController<Task, String> {
    private final TaskService service;
    private final TaskUiApiReadMapper readMapper;
    private final TaskUiApiSaveMapper saveMapper;

    @Autowired
    public TaskController(TaskService service, TaskUiApiReadMapper readMapper, TaskUiApiSaveMapper saveMapper) {
        this.readMapper = readMapper;
        this.saveMapper = saveMapper;
        this.service = service;
    }


    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping
    //Here we assume that Entity and DTO have:
    // 1) same field names (DTO can has only subset) due to @QuerydslPredicate is builded for Task.class, while api request is made for TaskDto.class
    // 3) bindings = .. is needed only for @ActiveProfile eq. "springJdbc" and "springJpa" where Repository bean is custom, so does not support bind with repository itself
    public Page<Task> findAll(@QuerydslPredicate(root = Task.class, bindings = TaskRepository.class) Predicate predicate, Pageable pageable, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.pageableEntityToDto(service.findAll(predicate, pageable, userDetails));
    }

    @RolesAllowed(Authority.Roles.ADMIN)
    @GetMapping(value = "/{id}")
    public Task findOne(@PathVariable("id") String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return readMapper.entityToDto(service.findById(id, userDetails));
    }

    @PostMapping
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody Task dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Task entity = saveMapper.dtoToCreateEntity(dto);
        return readMapper.entityToDto(service.create(entity, userDetails));
    }

    @PutMapping(value = "/{id}")
    @RolesAllowed(Authority.Roles.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public Task update(@RequestBody Task dto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Task entity = saveMapper.dtoToCreateEntity(dto);
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

