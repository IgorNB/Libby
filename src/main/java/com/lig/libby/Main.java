package com.lig.libby;

import com.lig.libby.security.config.AppPropertiesConfig;
import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.sql.SQLException;

@EnableConfigurationProperties(AppPropertiesConfig.class)
@SpringBootApplication
public class Main {
    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Main.class);
        Console.main(args);
    }
}
