package com.lig.libby.repository;

import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QAuthority;
import com.lig.libby.repository.core.jpa.GenericQueryDslRepositoryJpa;
import com.querydsl.core.BooleanBuilder;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Iterator;

@ThreadSafe
@Profile("springJpa")
@Repository
public class AuthorityRepositoryJpa extends GenericQueryDslRepositoryJpa<Authority, QAuthority, String> implements AuthorityRepository {

    public AuthorityRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Class<Authority> getDomainClass() {
        return Authority.class;
    }

    @Override
    public Authority getAuthorityByName(@NonNull String name) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(QAuthority.authority.name.eq(name));
        Iterator<Authority> iter = findAll(where).iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }
}
