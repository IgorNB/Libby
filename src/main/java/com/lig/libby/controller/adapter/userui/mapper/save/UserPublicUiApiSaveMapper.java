package com.lig.libby.controller.adapter.userui.mapper.save;

import com.lig.libby.controller.adapter.userui.dto.UserPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapperConfig;
import com.lig.libby.domain.User;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GenericUiApiSaveMapperConfig.class)
public interface UserPublicUiApiSaveMapper extends GenericUiApiSaveMapper<User, UserPublicDto> {
    @Override
    @Mapping(target = GenericAbstractPersistentAuditingObject.Fields.createdBy, ignore = true)
    @Mapping(target = GenericAbstractPersistentAuditingObject.Fields.lastUpdBy, ignore = true)
    @Mapping(target = GenericAbstractPersistentAuditingObject.Fields.createdDate, ignore = true)
    @Mapping(target = GenericAbstractPersistentAuditingObject.Fields.updatedDate, ignore = true)
    @Mapping(target = User.Fields.email, ignore = true)
    @Mapping(target = User.Fields.imageUrl, ignore = true)
    @Mapping(target = User.Fields.emailVerified, ignore = true)
    @Mapping(target = User.Fields.password, ignore = true)
    @Mapping(target = User.Fields.provider, ignore = true)
    @Mapping(target = User.Fields.providerId, ignore = true)
    @Mapping(target = User.Fields.authorities, ignore = true)
    User dtoToCreateEntity(UserPublicDto source);


    @Override
    @InheritConfiguration
    void dtoToUpdateEntity(UserPublicDto dto, @MappingTarget User toBeUpdatedEntity);
}


