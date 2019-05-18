package com.lig.libby.controller.adapter.adminui.mapper.read;

import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapper;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapperConfig;
import com.lig.libby.domain.User;
import org.mapstruct.Mapper;


@Mapper(config = GenericUiApiReadMapperConfig.class)
public interface UserUiApiReadMapper extends GenericUiApiReadMapper<User, User> {
}


