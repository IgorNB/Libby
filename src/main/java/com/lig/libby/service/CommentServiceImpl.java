package com.lig.libby.service;

import com.lig.libby.domain.Comment;
import com.lig.libby.domain.QComment;
import com.lig.libby.repository.CommentRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@ThreadSafe
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(@NonNull CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public @NonNull Page<Comment> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        if(predicate == null) {
            predicate = new BooleanBuilder().and(QComment.comment.id.isNotNull());
        }
        return commentRepository.findAll(predicate, pageable);
    }

    @NonNull
    @Override
    public Comment update(@NonNull Comment entity, @NonNull UserDetails userDetails) {
        return commentRepository.saveAndFind(entity);
    }

    @NonNull
    @Override
    public Comment create(@NonNull Comment entity, @NonNull UserDetails userDetails) {
        return commentRepository.saveAndFind(entity);
    }

    @Override
    public void deleteById(@NonNull String id) {
        commentRepository.deleteById(id);
    }
}
