package com.training.fitflow.facade;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.service.TraineeService;
import com.training.fitflow.service.TrainerService;
import com.training.fitflow.service.TrainingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GymFacade Tests")
public class GymFacadeTest {
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade facade;

    @Test
    @DisplayName("CreateTrainee → should delegate to service")
    void createTrainee_shouldDelegate() {
        Trainee trainee = new Trainee();
        when(traineeService.create(trainee)).thenReturn(trainee);

        Trainee result = facade.createTrainee(trainee);

        assertEquals(trainee, result);
        verify(traineeService).create(trainee);
    }

    @Test
    @DisplayName("GetTrainee → should return trainee from service")
    void getTrainee_shouldReturn() {
        Trainee trainee = new Trainee();
        when(traineeService.getByUsername(1L)).thenReturn(trainee);

        Trainee result = facade.getTrainee(1L);

        assertEquals(trainee, result);
    }

    @Test
    @DisplayName("GetAllTrainees → should return list")
    void getAllTrainees_shouldReturnList() {
        when(traineeService.getAll()).thenReturn(List.of(new Trainee()));

        List<Trainee> result = facade.getAllTrainees();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("DeleteTrainee → should call service delete")
    void deleteTrainee_shouldCallService() {
        facade.deleteTrainee(1L);

        verify(traineeService).deleteById(1L);
    }

    @Test
    @DisplayName("CreateTrainer → should delegate to service")
    void createTrainer_shouldDelegate() {
        Trainer trainer = new Trainer();
        when(trainerService.create(trainer)).thenReturn(trainer);

        Trainer result = facade.createTrainer(trainer);

        assertEquals(trainer, result);
        verify(trainerService).create(trainer);
    }

    @Test
    @DisplayName("GetTrainer → should return trainer")
    void getTrainer_shouldReturn() {
        Trainer trainer = new Trainer();
        when(trainerService.getById(1L)).thenReturn(trainer);

        Trainer result = facade.getTrainer(1L);

        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("GetAllTrainers → should return list")
    void getAllTrainers_shouldReturnList() {
        when(trainerService.getAll()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = facade.getAllTrainers();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("CreateTraining → should delegate to service")
    void createTraining_shouldDelegate() {
        Training training = new Training(
                1L, 1L, 1L,
                "Test",
                TrainingType.FITNESS,
                LocalDate.now(),
                60
        );

        when(trainingService.create(training)).thenReturn(training);

        Training result = facade.createTraining(training);

        assertEquals(training, result);
        verify(trainingService).create(training);
    }

    @Test
    @DisplayName("GetTraining → should return training")
    void getTraining_shouldReturn() {
        Training training = new Training();
        when(trainingService.getById(1L)).thenReturn(training);

        Training result = facade.getTraining(1L);

        assertEquals(training, result);
    }

    @Test
    @DisplayName("GetAllTrainings → should return list")
    void getAllTrainings_shouldReturnList() {
        when(trainingService.getAll()).thenReturn(List.of(new Training()));

        List<Training> result = facade.getAllTrainings();

        assertEquals(1, result.size());
    }
}
