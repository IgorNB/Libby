package com.lig.libby.service;

import com.lig.libby.domain.Comment;
import com.lig.libby.repository.CommentRepository;
import com.lig.libby.service.core.GenericFallbackUIAPIService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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
public class CommentServiceImpl implements CommentService, GenericFallbackUIAPIService<Comment, String> {
    private static final String CMD_KEY_PRX = "CommentService_";
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(@NonNull CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @HystrixCommand(fallbackMethod = FIND_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + FIND_BY_ID_FALLBACK)
    @Override
    public Comment findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return commentRepository.findById(id).orElse(null);
    }

    @HystrixCommand(fallbackMethod = FIND_ALL_FALLBACK, commandKey = CMD_KEY_PRX + FIND_ALL_FALLBACK)
    @Override
    public @NonNull Page<Comment> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        return commentRepository.findAll(predicate, pageable);
    }

    @HystrixCommand(fallbackMethod = UPDATE_FALLBACK, commandKey = CMD_KEY_PRX + UPDATE_FALLBACK)
    @NonNull
    @Override
    public Comment update(@NonNull Comment entity, @NonNull UserDetails userDetails) {
        return commentRepository.save(entity);
    }

    @HystrixCommand(fallbackMethod = CREATE_FALLBACK, commandKey = CMD_KEY_PRX + CREATE_FALLBACK)
    @NonNull
    @Override
    public Comment create(@NonNull Comment entity, @NonNull UserDetails userDetails) {
        return commentRepository.save(entity);
    }

    @HystrixCommand(fallbackMethod = DELETE_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + DELETE_BY_ID_FALLBACK)
    @Override
    public void deleteById(@NonNull String id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment findByIdFallback(@NonNull String id, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public @NonNull Page<Comment> findAllFallback(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public Comment updateFallback(@NonNull Comment entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public Comment createFallback(@NonNull Comment entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public void deleteByIdFallback(@NonNull String id, Throwable cause) {
        throw apiFallbackException(cause);
    }
}
