package com.training.fitflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FitFlowApplication {
    public static void main(String[] args) {
        log.info("Starting FitFlow application...");
        SpringApplication.run(FitFlowApplication.class, args);

        log.info("Application finished execution");
    }
}
