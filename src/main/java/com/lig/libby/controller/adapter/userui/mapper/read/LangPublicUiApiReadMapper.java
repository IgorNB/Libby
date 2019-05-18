package com.lig.libby.controller.adapter.userui.mapper.read;

import com.lig.libby.controller.adapter.userui.dto.LangPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapper;
import com.lig.libby.controller.core.uiadapter.mapper.read.GenericUiApiReadMapperConfig;
import com.lig.libby.domain.Lang;
import org.mapstruct.Mapper;

@Mapper(config = GenericUiApiReadMapperConfig.class, uses = {UserPublicUiApiReadMapper.class})
public interface LangPublicUiApiReadMapper extends GenericUiApiReadMapper<Lang, LangPublicDto> {
}


