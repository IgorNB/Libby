package com.lig.libby.controller.adapter.config;

import com.lig.libby.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
//spring batch test does not support @Transactional on test
//so we'll use separate DB by overriding property "spring.datasource.url" with unique name for this test
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {Main.class})
@ActiveProfiles({"BatchConfigurationTest", "springDataJpa", "shellDisabled"})
@TestPropertySource(properties = {"spring.batch.job.enabled=false", "migrationFromKaggleDataSetJob.ratingSkipLines=980000", "spring.datasource.url= jdbc:h2:mem:BatchConfigurationTest"})
public class BatchConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat("COMPLETED").isEqualTo(jobExecution.getExitStatus().getExitCode());
    }

    @TestConfiguration
    @Profile("BatchConfigurationTest")
    public static class BatchTestConfig {
        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }

}
