package ru.jetlabs.acquiringmockbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AcquiringMockBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcquiringMockBackendApplication.class, args);
    }

}
