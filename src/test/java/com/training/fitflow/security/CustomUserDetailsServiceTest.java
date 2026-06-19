package com.training.fitflow.security;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUsername("john.trainee");
        trainee.setPassword("$2a$10$hashedPassword");
        trainee.setActive(true);

        trainer = new Trainer();
        trainer.setUsername("john.trainer");
        trainer.setPassword("$2a$10$hashedPassword");
        trainer.setActive(true);
    }

    // ───────────────── loadUserByUsername ─────────────────

    @Test
    @DisplayName("loadUserByUsername → trainee found → returns correct UserDetails")
    void loadUserByUsername_traineeFound_returnsUserDetails() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        UserDetails result = userDetailsService.loadUserByUsername("john.trainee");

        assertEquals("john.trainee", result.getUsername());
        assertEquals("$2a$10$hashedPassword", result.getPassword());
        assertTrue(result.isEnabled());
        verifyNoInteractions(trainerRepository);
    }

    @Test
    @DisplayName("loadUserByUsername → trainer found → returns correct UserDetails")
    void loadUserByUsername_trainerFound_returnsUserDetails() {
        when(traineeRepository.findByUsername("john.trainer")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("john.trainer")).thenReturn(Optional.of(trainer));

        UserDetails result = userDetailsService.loadUserByUsername("john.trainer");

        assertEquals("john.trainer", result.getUsername());
        assertTrue(result.isEnabled());
    }

    @Test
    @DisplayName("loadUserByUsername → inactive trainee → UserDetails disabled")
    void loadUserByUsername_inactiveTrainee_userDetailsDisabled() {
        trainee.setActive(false);
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        UserDetails result = userDetailsService.loadUserByUsername("john.trainee");

        assertFalse(result.isEnabled());
    }

    @Test
    @DisplayName("loadUserByUsername → user not found → UsernameNotFoundException")
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    @DisplayName("loadUserByUsername → user has ROLE_USER authority")
    void loadUserByUsername_traineeFound_hasUserRole() {
        when(traineeRepository.findByUsername("john.trainee")).thenReturn(Optional.of(trainee));

        UserDetails result = userDetailsService.loadUserByUsername("john.trainee");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}