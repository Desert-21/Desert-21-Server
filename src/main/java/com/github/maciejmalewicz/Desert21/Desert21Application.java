package com.github.maciejmalewicz.Desert21;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class Desert21Application {

    public static void main(String[] args) {
        SpringApplication.run(Desert21Application.class, args);
    }

}
