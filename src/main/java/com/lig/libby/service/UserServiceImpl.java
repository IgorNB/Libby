package com.lig.libby.service;

import com.lig.libby.domain.QUser;
import com.lig.libby.domain.User;
import com.lig.libby.repository.UserRepository;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public @NonNull Page<User> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        if (predicate == null) {
            predicate = new BooleanBuilder().and(QUser.user.id.isNotNull());
        }
        return userRepository.findAll(predicate, pageable);
    }

    @NonNull
    @Override
    public User update(@NonNull User entity, @NonNull UserDetails userDetails) {
        return userRepository.saveAndFind(entity);
    }

    @NonNull
    @Override
    public User create(@NonNull User entity, @NonNull UserDetails userDetails) {
        return userRepository.saveAndFind(entity);
    }

    @Override
    public void deleteById(@NonNull String id) {
        userRepository.deleteById(id);
    }
}
