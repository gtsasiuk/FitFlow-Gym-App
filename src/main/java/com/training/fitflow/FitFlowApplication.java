package com.training.fitflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FitFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitFlowApplication.class, args);
    }
}
