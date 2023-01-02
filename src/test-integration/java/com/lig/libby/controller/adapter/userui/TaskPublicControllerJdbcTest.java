package com.lig.libby.controller.adapter.userui;

import com.lig.libby.Main;
import com.lig.libby.repository.TaskRepositoryJdbc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.batch.job.enabled=false"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
@ActiveProfiles({"shellDisabled", "springJdbc"})
public class TaskPublicControllerJdbcTest extends TaskPublicControllerTest {
    @Test
    public void testRepositoryInterfaceImplementationAutowiring() {
        assertThat(super.taskRepository instanceof TaskRepositoryJdbc
                || AopUtils.getTargetClass(super.taskRepository).equals(TaskRepositoryJdbc.class)
        ).isTrue();
    }
}
