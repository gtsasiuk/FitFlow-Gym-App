package com.training.fitflow;

import com.training.fitflow.config.AppConfig;
import com.training.fitflow.facade.GymFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class FitFlowApplication {
    static void main() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        GymFacade facade = context.getBean(GymFacade.class);

        System.out.println(facade.getTrainee(1L));

    }
}
