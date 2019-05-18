package com.lig.libby.controller.adapter.userui.mapper.read;

import com.lig.libby.controller.adapter.userui.dto.WorkPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapper;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapperConfig;
import com.lig.libby.domain.Work;
import org.mapstruct.Mapper;

@Mapper(config = GenericUiApiReadMapperConfig.class, uses = {UserPublicUiApiReadMapper.class, BookPublicUiApiReadMapper.class})
public interface WorkPublicUiApiReadMapper extends GenericUiApiReadMapper<Work, WorkPublicDto> {
}


