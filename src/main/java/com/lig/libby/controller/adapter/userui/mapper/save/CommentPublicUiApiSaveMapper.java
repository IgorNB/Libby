package com.lig.libby.controller.adapter.userui.mapper.save;

import com.lig.libby.controller.adapter.userui.dto.CommentPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapperConfig;
import com.lig.libby.domain.Comment;
import org.mapstruct.Mapper;

@Mapper(config = GenericUiApiSaveMapperConfig.class)
public interface CommentPublicUiApiSaveMapper extends GenericUiApiSaveMapper<Comment, CommentPublicDto> {
}


