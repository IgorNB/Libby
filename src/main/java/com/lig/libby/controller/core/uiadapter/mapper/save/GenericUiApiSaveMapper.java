package com.lig.libby.controller.core.uiadapter.mapper.save;

import org.mapstruct.MappingTarget;

public interface GenericUiApiSaveMapper<E, D> {
    E dtoToCreateEntity(D dto);

    void dtoToUpdateEntity(D dto, @MappingTarget E toBeUpdatedEntity);
}


