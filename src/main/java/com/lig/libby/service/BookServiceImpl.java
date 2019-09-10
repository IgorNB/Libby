package com.lig.libby.service;

import com.lig.libby.domain.Book;
import com.lig.libby.repository.BookRepository;
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
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(@NonNull BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public @NonNull Page<Book> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        return bookRepository.findAll(predicate, pageable);
    }

    @NonNull
    @Override
    public Book update(@NonNull Book entity, @NonNull UserDetails userDetails) {
        return bookRepository.save(entity);
    }

    @NonNull
    @Override
    public Book create(@NonNull Book entity, @NonNull UserDetails userDetails) {
        return bookRepository.save(entity);
    }

    @Override
    public void deleteById(@NonNull String id) {
        bookRepository.deleteById(id);
    }
}
