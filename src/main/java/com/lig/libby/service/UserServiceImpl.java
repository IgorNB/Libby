package com.lig.libby.service;

import com.lig.libby.domain.User;
import com.lig.libby.repository.UserRepository;
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
public class UserServiceImpl implements UserService, GenericFallbackUIAPIService<User, String> {
    private static final String CMD_KEY_PRX = "UserService_";
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @HystrixCommand(fallbackMethod = FIND_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + FIND_BY_ID_FALLBACK)
    public User findById(@NonNull String id, @NonNull UserDetails userDetails) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @HystrixCommand(fallbackMethod = FIND_ALL_FALLBACK, commandKey = CMD_KEY_PRX + FIND_ALL_FALLBACK)
    public @NonNull Page<User> findAll(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails) {
        return userRepository.findAll(predicate, pageable);
    }

    @NonNull
    @Override
    @HystrixCommand(fallbackMethod = UPDATE_FALLBACK, commandKey = CMD_KEY_PRX + UPDATE_FALLBACK)
    public User update(@NonNull User entity, @NonNull UserDetails userDetails) {
        return userRepository.save(entity);
    }

    @NonNull
    @Override
    @HystrixCommand(fallbackMethod = CREATE_FALLBACK, commandKey = CMD_KEY_PRX + CREATE_FALLBACK)
    public User create(@NonNull User entity, @NonNull UserDetails userDetails) {
        return userRepository.save(entity);
    }

    @Override
    @HystrixCommand(fallbackMethod = DELETE_BY_ID_FALLBACK, commandKey = CMD_KEY_PRX + DELETE_BY_ID_FALLBACK)
    public void deleteById(@NonNull String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByIdFallback(@NonNull String id, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public @NonNull Page<User> findAllFallback(Predicate predicate, Pageable pageable, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public User updateFallback(@NonNull User entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public User createFallback(@NonNull User entity, @NonNull UserDetails userDetails, Throwable cause) {
        throw apiFallbackException(cause);
    }

    @Override
    public void deleteByIdFallback(@NonNull String id, Throwable cause) {
        throw apiFallbackException(cause);
    }
}
