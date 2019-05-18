package com.lig.libby.controller.core.uiadapter.mapper.save;

import com.lig.libby.domain.core.PersistentObject;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
public class GenericReferenceMapperFastImpl implements GenericReferenceMapper {

    final
    EntityManager entityManager;

    @Autowired
    public GenericReferenceMapperFastImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <E extends PersistentObject> E getReferenceHavingOnlyId(PersistentObject dto, @TargetType Class<E> entityClass) {
        return dto != null ? entityManager.getReference(entityClass, dto.getId()) : null;
    }


}