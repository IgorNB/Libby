package com.lig.libby.controller.adapter.userui.mapper.save;

import com.lig.libby.controller.adapter.userui.dto.WorkPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapperConfig;
import com.lig.libby.domain.Work;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GenericUiApiSaveMapperConfig.class)
public interface WorkPublicUiApiSaveMapper extends GenericUiApiSaveMapper<Work, WorkPublicDto> {
    @Override
    @Mapping(target = Work.Fields.bestBook, ignore = true)
    Work dtoToCreateEntity(WorkPublicDto source);

    @Override
    @InheritConfiguration
    void dtoToUpdateEntity(WorkPublicDto dto, @MappingTarget Work toBeUpdatedEntity);
}


