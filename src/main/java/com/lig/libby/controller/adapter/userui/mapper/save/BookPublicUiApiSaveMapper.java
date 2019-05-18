package com.lig.libby.controller.adapter.userui.mapper.save;

import com.lig.libby.controller.adapter.userui.dto.BookPublicDto;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapper;
import com.lig.libby.controller.core.uiadapter.mapper.save.GenericUiApiSaveMapperConfig;
import com.lig.libby.domain.Book;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GenericUiApiSaveMapperConfig.class)
public interface BookPublicUiApiSaveMapper extends GenericUiApiSaveMapper<Book, BookPublicDto> {

    @Override
    @Mapping(target = Book.Fields.averageRating, ignore = true)
    @Mapping(target = Book.Fields.ratingsCount, ignore = true)
    @Mapping(target = Book.Fields.ratings1, ignore = true)
    @Mapping(target = Book.Fields.ratings2, ignore = true)
    @Mapping(target = Book.Fields.ratings3, ignore = true)
    @Mapping(target = Book.Fields.ratings4, ignore = true)
    @Mapping(target = Book.Fields.ratings5, ignore = true)
    Book dtoToCreateEntity(BookPublicDto source);

    @Override
    @InheritConfiguration
    void dtoToUpdateEntity(BookPublicDto dto, @MappingTarget Book toBeUpdatedEntity);
}


