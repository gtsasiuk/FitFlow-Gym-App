package com.training.fitflow.service;

import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.UserBlockedException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.security.BruteForceProtectionService;
import com.training.fitflow.security.TokenBlacklistService;
import com.training.fitflow.security.jwt.JwtTokenProvider;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BruteForceProtectionService bruteForceProtectionService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
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
        trainee.setPassword("$2a$10$hashedPassword");
        trainee.setActive(true);

        trainer = new Trainer();
        trainer.setId(2L);
        trainer.setUsername("john.trainer");
        trainer.setPassword("$2a$10$hashedPassword");
        trainer.setActive(true);
    }

    // ───────────────── login ─────────────────

    @Test
    @DisplayName("login → valid credentials → returns token")
    void login_validCredentials_returnsToken() {
        when(bruteForceProtectionService.isBlocked("john.trainee")).thenReturn(false);
        when(jwtTokenProvider.generateToken("john.trainee")).thenReturn("jwt-token");

        String token = authService.login("john.trainee", "pass123");

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("john.trainee", "pass123")
        );
        verify(bruteForceProtectionService).resetAttempts("john.trainee");
        verify(loginSuccessCounter).increment();
        verify(loginFailureCounter, never()).increment();
    }

    @Test
    @DisplayName("login → user blocked → UserBlockedException")
    void login_userBlocked_throwsUserBlockedException() {
        when(bruteForceProtectionService.isBlocked("john.trainee")).thenReturn(true);

        assertThrows(UserBlockedException.class,
                () -> authService.login("john.trainee", "pass123")
        );

        verifyNoInteractions(authenticationManager);
    }

    @Test
    @DisplayName("login → wrong password → BadCredentialException")
    void login_wrongPassword_throwsBadCredentialException() {
        when(bruteForceProtectionService.isBlocked("john.trainee")).thenReturn(false);
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any());

        assertThrows(BadCredentialException.class,
                () -> authService.login("john.trainee", "wrong")
        );

        verify(bruteForceProtectionService).registerFailure("john.trainee");
    }

    @Test
    @DisplayName("login → user disabled → UserDeactivatedException")
    void login_userDisabled_throwsUserDeactivatedException() {
        when(bruteForceProtectionService.isBlocked("john.trainee")).thenReturn(false);
        doThrow(new DisabledException("User disabled"))
                .when(authenticationManager)
                .authenticate(any());

        assertThrows(UserDeactivatedException.class,
                () -> authService.login("john.trainee", "pass123")
        );
    }

    // ───────────────── logout ─────────────────

    @Test
    @DisplayName("logout → valid token → token blacklisted")
    void logout_validToken_tokenBlacklisted() {
        authService.logout("jwt-token");

        verify(tokenBlacklistService).blacklist("jwt-token");
    }

    // ───────────────── changePassword ─────────────────

    @Test
    @DisplayName("changePassword → trainee valid old password → password changed")
    void changePassword_trainee_validOldPassword_success() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("pass123", trainee.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("$2a$10$newHashedPassword");

        authService.changePassword("john.trainee", "pass123", "newPass");

        assertEquals("$2a$10$newHashedPassword", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("changePassword → trainee wrong old password → BadCredentialException")
    void changePassword_trainee_wrongOldPassword_throwsBadCredential() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("wrong", trainee.getPassword())).thenReturn(false);

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
        when(passwordEncoder.matches("pass123", trainer.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("$2a$10$newHashedPassword");

        authService.changePassword("john.trainer", "pass123", "newPass");

        assertEquals("$2a$10$newHashedPassword", trainer.getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    @DisplayName("changePassword → trainer wrong old password → BadCredentialException")
    void changePassword_trainer_wrongOldPassword_throwsBadCredential() {
        when(traineeRepository.findByUsername("john.trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("john.trainer")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("wrong", trainer.getPassword())).thenReturn(false);

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

        verify(traineeRepository, never()).save(any());
        verify(trainerRepository, never()).save(any());
    }
}