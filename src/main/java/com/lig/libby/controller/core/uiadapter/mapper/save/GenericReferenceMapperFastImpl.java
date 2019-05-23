package com.lig.libby.controller.core.uiadapter.mapper.save;

import com.lig.libby.domain.core.PersistentObject;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;


@Service
public class GenericReferenceMapperFastImpl implements GenericReferenceMapper {

    final
    MongoOperations entityManager;

    @Autowired
    public GenericReferenceMapperFastImpl(MongoOperations entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <E extends PersistentObject> E getReferenceHavingOnlyId(PersistentObject dto, @TargetType Class<E> entityClass) {
        return dto != null ? entityManager.findById(dto.getId(), entityClass) : null;
    }


}