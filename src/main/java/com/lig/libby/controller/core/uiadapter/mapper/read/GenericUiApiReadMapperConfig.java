package com.lig.libby.controller.core.uiadapter.mapper.read;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface GenericUiApiReadMapperConfig {
}
