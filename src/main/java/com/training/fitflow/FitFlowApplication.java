package com.training.fitflow;

import com.training.fitflow.config.AppConfig;
import com.training.fitflow.facade.GymFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@Slf4j
public class FitFlowApplication {
    public static void main(String[] args) {
        log.info("Starting FitFlow application...");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        log.info("Spring context started successfully");
        GymFacade facade = context.getBean(GymFacade.class);

        log.debug("Fetching all trainees:, count={}", facade.getAllTrainees().size());
        facade.getAllTrainees().forEach(trainee -> log.info("Found trainee: {}", trainee));

        log.debug("Fetching all trainers:, count={}", facade.getAllTrainers().size());
        facade.getAllTrainers().forEach(trainer -> log.info("Found trainer: {}", trainer));

        log.debug("Fetching all trainings:, count={}", facade.getAllTrainings().size());
        facade.getAllTrainings().forEach(training -> log.info("Found training: {}", training));

        try {
            log.debug("Attempting to fetch non-existent trainee with id=999");
            facade.getTrainee("eugene.tsasiuk");
        } catch (Exception e) {
            log.error("Expected error occurred: {}", e.getMessage());
        }

        log.info("Application finished execution");
    }
}
