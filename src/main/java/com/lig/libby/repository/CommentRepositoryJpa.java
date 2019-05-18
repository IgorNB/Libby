package com.lig.libby.repository;

import com.lig.libby.domain.Comment;
import com.lig.libby.domain.QComment;
import com.lig.libby.repository.core.jpa.GenericQueryDslRepositoryJpa;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@ThreadSafe
@Profile("springJpa")
@Repository
public class CommentRepositoryJpa extends GenericQueryDslRepositoryJpa<Comment, QComment, String> implements CommentRepository {

    public CommentRepositoryJpa(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Class<Comment> getDomainClass() {
        return Comment.class;
    }
}
