package com.fcfs.fcfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class FcfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcfsApplication.class, args);
    }

}
