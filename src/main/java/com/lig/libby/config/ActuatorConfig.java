package com.lig.libby.config;

import com.lig.libby.domain.QTask;
import com.lig.libby.domain.Task;
import com.lig.libby.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {
    private final TaskRepository taskRepository;

    @Autowired
    public ActuatorConfig(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Bean
    public HealthIndicator totalTasksSubmittedHealthIndicator() {
        return () -> {
            long cnt = taskRepository.count(new BooleanBuilder().and(QTask.task.workflowStep.eq(Task.WorkflowStepEnum.SUBMITTED)));
            if (cnt > 100) {
                return Health.down().withDetail("message", "Too many user tasks in work! Number of submitted tasks: " + cnt).withDetail("value", cnt).build();
            } else {
                return Health.up().withDetail("message", "Number of submitted tasks: " + cnt).withDetail("value", cnt).build();
            }
        };
    }

}