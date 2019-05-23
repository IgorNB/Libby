package com.lig.libby.repository.common;

import com.lig.libby.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class DataJpaAuditConfig {
    @Bean
    public AuditorAware<User> auditorProvider(@Autowired MongoOperations entityManager) {
        return () -> {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            return Optional.ofNullable(securityContext.getAuthentication())
                    .map(authentication -> null //in dataJPA tests we do not log createdBy and updatedBy
                    );
        };
    }
}
