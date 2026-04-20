package com.training.fitflow.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import com.training.fitflow.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataLoaderTest {
    private InMemoryStorage storage;
    private ObjectMapper objectMapper;
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        objectMapper = mock(ObjectMapper.class);

        dataLoader = new DataLoader(storage, objectMapper);

        dataLoader.setTraineesDataPath("/data/trainees.json");
        dataLoader.setTrainersDataPath("/data/trainers.json");
        dataLoader.setTrainingDataPath("/data/trainings.json");
    }

    @Test
    void loadData_shouldLoadAllEntitiesIntoStorage() throws Exception {
        List<Trainee> trainees = List.of(
                new Trainee(1L, "John", "Doe", "u", "p", true,
                        LocalDate.of(2000,1,1), "addr")
        );

        List<Trainer> trainers = List.of(
                new Trainer(1L, "Mike", "Tyson", "u", "p", true,
                        TrainingType.FITNESS)
        );

        List<Training> trainings = List.of(
                new Training(1L, 1L, 1L, "Training",
                        TrainingType.FITNESS,
                        LocalDate.of(2025,1,1),
                        60)
        );

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(trainees)
                .thenReturn(trainers)
                .thenReturn(trainings);

        dataLoader.loadData();

        assertEquals(1, storage.getTrainees().size());
        assertEquals(1, storage.getTrainers().size());
        assertEquals(1, storage.getTrainings().size());
    }
}