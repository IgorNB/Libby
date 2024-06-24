package com.lig.libby.repository.core;

import com.lig.libby.domain.core.PersistentObject;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("never")
@SuppressWarnings("squid:S2326")
public interface GenericUiApiRepository<E extends PersistentObject> extends MongoRepository<E, String> {

    //we should not use standart save because it returns @DbRef object as it was populated before saving (only id if only id was populated), but if we run findById(), then @DbRef object weill have all fields populated.
    // E.g. entity = repo.save(entity) is !!!NOT!!! repo.save(entity); entity = repo.findById(entity.getId())
    default E saveAndFind(E entity) {
        save(entity);
        return findById(entity.getId()).orElse(null);
    }
}