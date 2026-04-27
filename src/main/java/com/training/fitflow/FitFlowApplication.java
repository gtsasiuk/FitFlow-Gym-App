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

        GymFacade facade = context.getBean(GymFacade.class);

        log.info("Application finished execution");
    }
}
