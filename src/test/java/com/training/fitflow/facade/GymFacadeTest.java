package com.training.fitflow.facade;

import com.training.fitflow.model.*;
import com.training.fitflow.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GymFacade Tests")
class GymFacadeTest {
    @Mock
    private AuthService authService;
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
    @DisplayName("UpdateTrainee → should authenticate and update")
    void updateTrainee_shouldDelegate() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(authService.authenticateTrainee("u", "p")).thenReturn(new Trainee());
        when(traineeService.update(trainee)).thenReturn(trainee);

        Trainee result = facade.updateTrainee("u", "p", trainee);

        assertEquals(trainee, result);
        verify(traineeService).update(trainee);
    }

    @Test
    @DisplayName("GetTrainee → should return trainee")
    void getTrainee_shouldReturn() {
        Trainee auth = new Trainee();
        auth.setUsername("john");

        when(authService.authenticateTrainee("john", "pass")).thenReturn(auth);
        when(traineeService.getByUsername("john")).thenReturn(auth);

        Trainee result = facade.getTrainee("john", "pass");

        assertEquals(auth, result);
    }

    @Test
    @DisplayName("GetAllTrainees → should return list")
    void getAllTrainees_shouldReturnList() {
        when(authService.authenticateTrainee("u", "p")).thenReturn(new Trainee());
        when(traineeService.getAll()).thenReturn(List.of(new Trainee()));

        List<Trainee> result = facade.getAllTrainees("u", "p");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("DeleteTrainee → should call service")
    void deleteTrainee_shouldCallService() {
        Trainee auth = new Trainee();
        auth.setUsername("john");

        when(authService.authenticateTrainee("john", "pass")).thenReturn(auth);

        facade.deleteTrainee("john", "pass");

        verify(traineeService).deleteByUsername("john");
    }

    @Test
    @DisplayName("ActivateTrainee → should activate")
    void activateTrainee_shouldCallService() {
        Trainee auth = new Trainee();
        auth.setUsername("john");

        when(authService.authenticateTrainee("john", "pass")).thenReturn(auth);

        facade.activateTrainee("john", "pass");

        verify(traineeService).activate("john");
    }

    @Test
    @DisplayName("DeactivateTrainee → should deactivate")
    void deactivateTrainee_shouldCallService() {
        Trainee auth = new Trainee();
        auth.setUsername("john");

        when(authService.authenticateTrainee("john", "pass")).thenReturn(auth);

        facade.deactivateTrainee("john", "pass");

        verify(traineeService).deactivate("john");
    }

    @Test
    @DisplayName("UpdateTraineeTrainers → should update trainers")
    void updateTraineeTrainers_shouldCallService() {
        when(authService.authenticateTrainee("u", "p")).thenReturn(new Trainee());

        facade.updateTraineeTrainers("u", "p", List.of(1L, 2L));

        verify(traineeService).updateTraineeTrainers("u", List.of(1L, 2L));
    }

    @Test
    @DisplayName("ChangeTraineePassword → should change password")
    void changeTraineePassword_shouldCallService() {
        Trainee auth = new Trainee();
        auth.setUsername("john");

        when(authService.authenticateTrainee("john", "old")).thenReturn(auth);

        facade.changeTraineePassword("john", "old", "new");

        verify(traineeService).changePassword("john", "new");
    }

    @Test
    @DisplayName("CreateTrainer → should delegate")
    void createTrainer_shouldDelegate() {
        Trainer trainer = new Trainer();

        when(trainerService.create(trainer)).thenReturn(trainer);

        Trainer result = facade.createTrainer(trainer);

        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("GetTrainer → should return trainer")
    void getTrainer_shouldReturn() {
        Trainer auth = new Trainer();
        auth.setUsername("john");

        when(authService.authenticateTrainer("john", "pass")).thenReturn(auth);
        when(trainerService.getByUsername("john")).thenReturn(auth);

        Trainer result = facade.getTrainer("john", "pass");

        assertEquals(auth, result);
    }

    @Test
    @DisplayName("GetAllTrainers → should return list")
    void getAllTrainers_shouldReturnList() {
        when(authService.authenticateTrainer("u", "p")).thenReturn(new Trainer());
        when(trainerService.getAll()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = facade.getAllTrainers("u", "p");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("UpdateTrainer → should update trainer")
    void updateTrainer_shouldCallService() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(authService.authenticateTrainer("u", "p")).thenReturn(new Trainer());
        when(trainerService.update(trainer)).thenReturn(trainer);

        Trainer result = facade.updateTrainer("u", "p", trainer);

        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("ActivateTrainer → should activate")
    void activateTrainer_shouldCallService() {
        Trainer auth = new Trainer();
        auth.setUsername("john");

        when(authService.authenticateTrainer("john", "pass")).thenReturn(auth);

        facade.activateTrainer("john", "pass");

        verify(trainerService).activate("john");
    }

    @Test
    @DisplayName("DeactivateTrainer → should deactivate")
    void deactivateTrainer_shouldCallService() {
        Trainer auth = new Trainer();
        auth.setUsername("john");

        when(authService.authenticateTrainer("john", "pass")).thenReturn(auth);

        facade.deactivateTrainer("john", "pass");

        verify(trainerService).deactivate("john");
    }

    @Test
    @DisplayName("ChangeTrainerPassword → should change password")
    void changeTrainerPassword_shouldCallService() {
        Trainer auth = new Trainer();
        auth.setUsername("john");

        when(authService.authenticateTrainer("john", "old")).thenReturn(auth);

        facade.changeTrainerPassword("john", "old", "new");

        verify(trainerService).changePassword("john", "new");
    }

    @Test
    @DisplayName("CreateTraining → should delegate")
    void createTraining_shouldDelegate() {
        Training training = new Training(
                1L,
                new Trainee(),
                new Trainer(),
                "Test",
                new TrainingType(1L, "Fitness"),
                LocalDate.now(),
                60
        );

        when(trainingService.create(training)).thenReturn(training);

        Training result = facade.createTraining(training);

        assertEquals(training, result);
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

    @Test
    @DisplayName("GetTraineeTrainings → should return filtered list")
    void getTraineeTrainings_shouldReturnList() {
        when(authService.authenticateTrainee("u", "p")).thenReturn(new Trainee());
        when(trainingService.getTraineeTrainings(any(), any(), any(), any(), any()))
                .thenReturn(List.of(new Training()));

        List<Training> result = facade.getTraineeTrainings(
                "u", "p", null, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("GetTrainerTrainings → should return filtered list")
    void getTrainerTrainings_shouldReturnList() {
        when(authService.authenticateTrainer("u", "p")).thenReturn(new Trainer());
        when(trainingService.getTrainerTrainings(any(), any(), any(), any()))
                .thenReturn(List.of(new Training()));

        List<Training> result = facade.getTrainerTrainings(
                "u", "p", null, null, null);

        assertEquals(1, result.size());
    }
}