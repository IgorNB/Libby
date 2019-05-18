package com.lig.libby.controller.adapter.userui.mapper.save;

import com.lig.libby.controller.adapter.userui.dto.TaskPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapperConfig;
import com.lig.libby.domain.Task;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GenericUiApiSaveMapperConfig.class)
public interface TaskPublicUiApiSaveMapper extends GenericUiApiSaveMapper<Task, TaskPublicDto> {
    @Override
    @Mapping(target = Task.Fields.book, ignore = true)
    Task dtoToCreateEntity(TaskPublicDto source);

    @Override
    @InheritConfiguration
    void dtoToUpdateEntity(TaskPublicDto dto, @MappingTarget Task toBeUpdatedEntity);
}


