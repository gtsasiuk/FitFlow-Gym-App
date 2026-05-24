package com.training.fitflow.service;

import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import io.micrometer.core.instrument.Counter;
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
    @Mock
    private Counter loginSuccessCounter;
    @Mock
    private Counter loginFailureCounter;

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

    // ───────────────── authenticate ─────────────────

    @Test
    @DisplayName("authenticate → trainee valid credentials → success")
    void authenticate_trainee_validCredentials_success() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> authService.authenticate("john.trainee", "pass123"));

        verify(traineeRepository).findByUsername("john.trainee");
        verifyNoInteractions(trainerRepository);

        verify(loginSuccessCounter).increment();
        verify(loginFailureCounter, never()).increment();
    }

    @Test
    @DisplayName("authenticate → trainee wrong password → BadCredentialException")
    void authenticate_trainee_wrongPassword_throwsBadCredential() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        assertThrows(BadCredentialException.class,
                () -> authService.authenticate("john.trainee", "wrong")
        );

        verify(loginFailureCounter).increment();
        verify(loginSuccessCounter, never()).increment();
    }

    @Test
    @DisplayName("authenticate → trainee inactive → UserDeactivatedException")
    void authenticate_trainee_inactive_throwsUserDeactivated() {
        trainee.setActive(false);

        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        assertThrows(UserDeactivatedException.class,
                () -> authService.authenticate("john.trainee", "pass123")
        );

        verify(loginFailureCounter).increment();
        verify(loginSuccessCounter, never()).increment();
    }

    @Test
    @DisplayName("authenticate → trainer valid credentials → success")
    void authenticate_trainer_validCredentials_success() {
        when(traineeRepository.findByUsername("john.trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("john.trainer")).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> authService.authenticate("john.trainer", "pass123"));

        verify(trainerRepository).findByUsername("john.trainer");

        verify(loginSuccessCounter).increment();
        verify(loginFailureCounter, never()).increment();
    }

    @Test
    @DisplayName("authenticate → trainer wrong password → BadCredentialException")
    void authenticate_trainer_wrongPassword_throwsBadCredential() {
        when(traineeRepository.findByUsername("john.trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("john.trainer")).thenReturn(Optional.of(trainer));

        assertThrows(
                BadCredentialException.class,
                () -> authService.authenticate("john.trainer", "wrong")
        );

        verify(loginFailureCounter).increment();
        verify(loginSuccessCounter, never()).increment();
    }

    @Test
    @DisplayName("authenticate → trainer inactive → UserDeactivatedException")
    void authenticate_trainer_inactive_throwsUserDeactivated() {
        trainer.setActive(false);

        when(traineeRepository.findByUsername("john.trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("john.trainer")).thenReturn(Optional.of(trainer));

        assertThrows(UserDeactivatedException.class,
                () -> authService.authenticate("john.trainer", "pass123")
        );

        verify(loginFailureCounter).increment();
        verify(loginSuccessCounter, never()).increment();
    }

    @Test
    @DisplayName("authenticate → user not found → BadCredentialException")
    void authenticate_userNotFound_throwsBadCredential() {
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(BadCredentialException.class,
                () -> authService.authenticate("unknown", "pass123")
        );

        verify(loginSuccessCounter, never()).increment();
        verify(loginFailureCounter, never()).increment();
    }

    // ───────────────── changePassword ─────────────────

    @Test
    @DisplayName("changePassword → trainee valid old password → password changed")
    void changePassword_trainee_validOldPassword_success() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        authService.changePassword("john.trainee", "pass123", "newPass");

        assertEquals("newPass", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("changePassword → trainee wrong old password → BadCredentialException")
    void changePassword_trainee_wrongOldPassword_throwsBadCredential() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        assertThrows(BadCredentialException.class,
                () -> authService.changePassword("john.trainee", "wrong", "newPass")
        );

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword → trainer valid old password → password changed")
    void changePassword_trainer_validOldPassword_success() {
        when(traineeRepository.findByUsername("john.trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("john.trainer")).thenReturn(Optional.of(trainer));

        authService.changePassword("john.trainer", "pass123", "newPass");

        assertEquals("newPass", trainer.getPassword());

        verify(trainerRepository).save(trainer);
    }

    @Test
    @DisplayName("changePassword → trainer wrong old password → BadCredentialException")
    void changePassword_trainer_wrongOldPassword_throwsBadCredential() {
        when(traineeRepository.findByUsername("john.trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("john.trainer")).thenReturn(Optional.of(trainer));

        assertThrows(BadCredentialException.class,
                () -> authService.changePassword("john.trainer", "wrong", "newPass")
        );

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword → user not found → BadCredentialException")
    void changePassword_userNotFound_throwsBadCredential() {
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(BadCredentialException.class,
                () -> authService.changePassword("unknown", "pass123", "newPass")
        );
    }
}