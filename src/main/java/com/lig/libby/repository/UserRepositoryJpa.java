package com.lig.libby.repository;

import com.lig.libby.domain.QUser;
import com.lig.libby.domain.User;
import com.lig.libby.repository.core.jpa.GenericQueryDslRepositoryJpa;
import com.querydsl.core.BooleanBuilder;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Iterator;
import java.util.Optional;

@ThreadSafe
@Profile("springJpa")
@Repository
public class UserRepositoryJpa extends GenericQueryDslRepositoryJpa<User, QUser, String> implements UserRepository {

    public UserRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Class<User> getDomainClass() {
        return User.class;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(QUser.user.email.eq(email));
        Iterator<User> iterator = findAll(where).iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        return Optional.ofNullable(iterator.next());
    }
}
