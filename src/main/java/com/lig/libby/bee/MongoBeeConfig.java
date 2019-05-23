package com.lig.libby.bee;

import com.github.mongobee.Mongobee;
import com.lig.libby.bee.changelog.DatabaseChangelog;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoBeeConfig {


    @Bean
    public Mongobee mongobee(MongoClient mongoClient, Environment environment, MongoTemplate mongoTemplate) {
        Mongobee runner = new Mongobee(mongoClient);
        runner.setDbName("libby");
        runner.setMongoTemplate(mongoTemplate);
        runner.setChangeLogsScanPackage(DatabaseChangelog.class.getPackage().getName());
        runner.setEnabled(true);
        runner.setSpringEnvironment(environment);
        return runner;
    }
}
