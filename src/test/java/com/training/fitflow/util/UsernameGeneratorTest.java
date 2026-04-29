package com.training.fitflow.util;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsernameGenerator Tests")
class UsernameGeneratorTest {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private UsernameGenerator generator;

    @Test
    @DisplayName("Generate → should return base username when no duplicates")
    void generate_shouldReturnBase() {
        when(traineeRepository.findAll()).thenReturn(List.of());
        when(trainerRepository.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Doe");

        assertEquals("John.Doe", result);
    }

    @Test
    @DisplayName("Generate → should append number when duplicates exist")
    void generate_shouldAppendNumber() {
        Trainee t1 = new Trainee();
        t1.setUsername("John.Doe");

        Trainee t2 = new Trainee();
        t2.setUsername("John.Doe1");

        when(traineeRepository.findAll()).thenReturn(List.of(t1, t2));
        when(trainerRepository.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Doe");

        assertEquals("John.Doe2", result);
    }

    @Test
    @DisplayName("Generate → should count duplicates from trainers too")
    void generate_shouldIncludeTrainers() {
        Trainer trainer = new Trainer();
        trainer.setUsername("John.Doe");

        when(traineeRepository.findAll()).thenReturn(List.of());
        when(trainerRepository.findAll()).thenReturn(List.of(trainer));

        String result = generator.generate("John", "Doe");

        assertEquals("John.Doe1", result);
    }

    @Test
    @DisplayName("Generate → should ignore null usernames")
    void generate_shouldIgnoreNulls() {
        Trainee t1 = new Trainee();
        t1.setUsername(null);

        when(traineeRepository.findAll()).thenReturn(List.of(t1));
        when(trainerRepository.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Doe");

        assertEquals("John.Doe", result);
    }
}