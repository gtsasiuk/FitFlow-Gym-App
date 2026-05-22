package com.training.fitflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI fitFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitFlow REST API")
                        .description("Gym CRM system for managing trainers, trainees and training sessions")
                        .version("1.0"));
    }
}