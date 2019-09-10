package com.lig.libby.service;

import com.lig.libby.domain.Book;
import com.lig.libby.repository.BookRepository;
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
public class BookServiceImpl implements BookService, GenericFallbackUIAPIService<Book, String> {
    private static final String CMD_KEY_PRX = "BookService_";
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(@NonNull BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @HystrixCommand(fallbackMethod = FIND_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + FIND_BY_ID_FALLBACK)
    @Override
    public Book findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return bookRepository.findById(id).orElse(null);
    }

    @HystrixCommand(fallbackMethod = FIND_ALL_FALLBACK, commandKey = CMD_KEY_PRX + FIND_ALL_FALLBACK)
    @Override
    public @NonNull Page<Book> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        return bookRepository.findAll(predicate, pageable);
    }

    @HystrixCommand(fallbackMethod = UPDATE_FALLBACK, commandKey = CMD_KEY_PRX + UPDATE_FALLBACK)
    @NonNull
    @Override
    public Book update(@NonNull Book entity, @NonNull UserDetails userDetails) {
        return bookRepository.save(entity);
    }

    @HystrixCommand(fallbackMethod = CREATE_FALLBACK, commandKey = CMD_KEY_PRX + CREATE_FALLBACK)
    @NonNull
    @Override
    public Book create(@NonNull Book entity, @NonNull UserDetails userDetails) {
        return bookRepository.save(entity);
    }

    @HystrixCommand(fallbackMethod = DELETE_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + DELETE_BY_ID_FALLBACK)
    @Override
    public void deleteById(@NonNull String id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book findByIdFallback(@NonNull String id, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public @NonNull Page<Book> findAllFallback(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public Book updateFallback(@NonNull Book entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public Book createFallback(@NonNull Book entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public void deleteByIdFallback(@NonNull String id, Throwable cause) {
        throw apiFallbackException(cause);
    }
}
