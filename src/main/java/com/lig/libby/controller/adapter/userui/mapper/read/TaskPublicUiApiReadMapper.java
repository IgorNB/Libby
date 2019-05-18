package com.lig.libby.controller.adapter.userui.mapper.read;

import com.lig.libby.controller.adapter.userui.dto.TaskPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapper;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapperConfig;
import com.lig.libby.domain.Task;
import org.mapstruct.Mapper;

@Mapper(config = GenericUiApiReadMapperConfig.class, uses = {UserPublicUiApiReadMapper.class, WorkPublicUiApiReadMapper.class, LangPublicUiApiReadMapper.class, BookPublicUiApiReadMapper.class})
public interface TaskPublicUiApiReadMapper extends GenericUiApiReadMapper<Task, TaskPublicDto> {
}


