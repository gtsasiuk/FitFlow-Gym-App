package com.training.fitflow.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader {
    private final InMemoryStorage storage;
    private final ObjectMapper objectMapper;

    @Value("${storage.trainees.file}")
    private String traineesDataPath;

    @Value("${storage.trainers.file}")
    private String trainersDataPath;

    @Value("${storage.training.file}")
    private String trainingDataPath;

    @PostConstruct
    public void loadData() {
        log.info("Loading initial data into InMemoryStorage...");
        try {
            loadTrainees();
            loadTrainers();
            loadTrainings();
            log.info("Data loading completed successfully");
        } catch (Exception e) {
            log.error("Failed to load data", e);
        }
    }

    private void loadTrainees() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(traineesDataPath);
        if (inputStream != null) {
            List<Trainee> trainees = objectMapper.readValue(inputStream, new TypeReference<List<Trainee>>() {});
            trainees.forEach(trainee -> storage.getTrainees().put(trainee.getId(), trainee));
            log.info("Loaded {} trainees", trainees.size());
        }
    }

    private void loadTrainers() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(trainersDataPath);
        if (inputStream != null) {
            List<Trainer> trainers = objectMapper.readValue(inputStream, new TypeReference<List<Trainer>>() {});
            trainers.forEach(trainer -> storage.getTrainers().put(trainer.getId(), trainer));
            log.info("Loaded {} trainers", trainers.size());
        }
    }

    private void loadTrainings() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(trainingDataPath);
        if (inputStream != null) {
            List<Training> trainings = objectMapper.readValue(inputStream, new TypeReference<List<Training>>() {});
            trainings.forEach(training -> storage.getTrainings().put(training.getId(), training));
            log.info("Loaded {} trainings", trainings.size());
        }
    }
}

