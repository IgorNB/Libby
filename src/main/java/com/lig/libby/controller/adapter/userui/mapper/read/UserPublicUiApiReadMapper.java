package com.lig.libby.controller.adapter.userui.mapper.read;

import com.lig.libby.controller.adapter.userui.dto.UserPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapper;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapperConfig;
import com.lig.libby.domain.User;
import org.mapstruct.Mapper;

@Mapper(config = GenericUiApiReadMapperConfig.class)
public interface UserPublicUiApiReadMapper extends GenericUiApiReadMapper<User, UserPublicDto> {
}


