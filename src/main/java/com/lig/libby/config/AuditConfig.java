package com.lig.libby.config;

import com.lig.libby.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class AuditConfig {
    @Bean
    public AuditorAware<User> auditorProvider(@Autowired MongoOperations entityManager) {
        return () -> {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            return Optional.ofNullable(securityContext.getAuthentication())
                    .map(authentication -> {
                        if (authentication.getPrincipal() instanceof UserDetails) {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            return entityManager.findById(userDetails.getUsername(), User.class);
                        }
                        return null;
                    });
        };
    }
}
