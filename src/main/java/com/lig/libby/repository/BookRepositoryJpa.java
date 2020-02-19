package com.lig.libby.repository;

import com.lig.libby.domain.Book;
import com.lig.libby.domain.QBook;
import com.lig.libby.repository.core.jpa.GenericQueryDslRepositoryJpa;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@ThreadSafe
@Profile("springJpa")
@Repository
public class BookRepositoryJpa extends GenericQueryDslRepositoryJpa<Book, QBook, String> implements BookRepository {

    public BookRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Class<Book> getDomainClass() {
        return Book.class;
    }
}
