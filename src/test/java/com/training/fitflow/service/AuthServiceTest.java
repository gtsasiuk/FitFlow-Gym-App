package com.training.fitflow.service;

import com.training.fitflow.exception.*;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private AuthService authService;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("john.trainee");
        trainee.setPassword("pass123");
        trainee.setActive(true);

        trainer = new Trainer();
        trainer.setId(2L);
        trainer.setUsername("john.trainer");
        trainer.setPassword("pass123");
        trainer.setActive(true);
    }

    @Test
    @DisplayName("Authenticate Trainee → success login")
    void authenticateTrainee_shouldReturnTrainee_whenValidCredentials() {
        when(traineeRepository.findByUsername("john.trainee"))
                .thenReturn(Optional.of(trainee));

        Trainee result = authService.authenticateTrainee("john.trainee", "pass123");

        assertEquals(trainee, result);
        verify(traineeRepository).findByUsername("john.trainee");
    }

    @Test
    @DisplayName("Authenticate Trainee → wrong password")
    void authenticateTrainee_shouldThrowException_whenWrongPassword() {
        when(traineeRepository.findByUsername("john.trainee"))
                .thenReturn(Optional.of(trainee));

        assertThrows(BadCredentialException.class,
                () -> authService.authenticateTrainee("john.trainee", "wrong"));

        verify(traineeRepository).findByUsername("john.trainee");
    }

    @Test
    @DisplayName("Authenticate Trainee → inactive user")
    void authenticateTrainee_shouldThrowException_whenInactive() {
        trainee.setActive(false);

        when(traineeRepository.findByUsername("john.trainee"))
                .thenReturn(Optional.of(trainee));

        assertThrows(UserDeactivatedException.class,
                () -> authService.authenticateTrainee("john.trainee", "pass123"));
    }

    @Test
    @DisplayName("Authenticate Trainee → not found")
    void authenticateTrainee_shouldThrowException_whenNotFound() {
        when(traineeRepository.findByUsername("john.trainee"))
                .thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> authService.authenticateTrainee("john.trainee", "pass123"));
    }

    @Test
    @DisplayName("Authenticate Trainer → success login")
    void authenticateTrainer_shouldReturnTrainer_whenValidCredentials() {
        when(trainerRepository.findByUsername("john.trainer"))
                .thenReturn(Optional.of(trainer));

        Trainer result = authService.authenticateTrainer("john.trainer", "pass123");

        assertEquals(trainer, result);
        verify(trainerRepository).findByUsername("john.trainer");
    }

    @Test
    @DisplayName("Authenticate Trainer → wrong password")
    void authenticateTrainer_shouldThrowException_whenWrongPassword() {
        when(trainerRepository.findByUsername("john.trainer"))
                .thenReturn(Optional.of(trainer));

        assertThrows(BadCredentialException.class,
                () -> authService.authenticateTrainer("john.trainer", "wrong"));
    }

    @Test
    @DisplayName("Authenticate Trainer → inactive user")
    void authenticateTrainer_shouldThrowException_whenInactive() {
        trainer.setActive(false);

        when(trainerRepository.findByUsername("john.trainer"))
                .thenReturn(Optional.of(trainer));

        assertThrows(UserDeactivatedException.class,
                () -> authService.authenticateTrainer("john.trainer", "pass123"));
    }

    @Test
    @DisplayName("Authenticate Trainer → not found")
    void authenticateTrainer_shouldThrowException_whenNotFound() {
        when(trainerRepository.findByUsername("john.trainer"))
                .thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> authService.authenticateTrainer("john.trainer", "pass123"));
    }

    @Test
    @DisplayName("Authenticate → blank username should fail")
    void authenticate_shouldFail_whenUsernameBlank() {
        assertThrows(RuntimeException.class,
                () -> authService.authenticateTrainee("", "pass"));
    }

    @Test
    @DisplayName("Authenticate → blank password should fail")
    void authenticate_shouldFail_whenPasswordBlank() {
        assertThrows(RuntimeException.class,
                () -> authService.authenticateTrainer("user", ""));
    }
}