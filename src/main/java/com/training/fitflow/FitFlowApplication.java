package com.training.fitflow;

import com.training.fitflow.config.AppConfig;
import com.training.fitflow.facade.GymFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class FitFlowApplication {
    public static void main(String[] args) {
        System.out.println("Starting FitFlow application...");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("Spring context started successfully");
        GymFacade facade = context.getBean(GymFacade.class);

        System.out.println("Fetching all trainees:");
        facade.getAllTrainees().forEach(System.out::println);

        System.out.println("Fetching all trainers:");
        facade.getAllTrainers().forEach(System.out::println);

        System.out.println("Fetching all trainings:");
        facade.getAllTrainings().forEach(System.out::println);

        try {
            System.out.println("Attempting to fetch non-existent trainee with id=999");
            facade.getTrainee(999L);
        } catch (Exception e) {
            System.out.println("Expected error occurred: " + e.getMessage());
        }

        System.out.println("Application finished execution");
    }
}
