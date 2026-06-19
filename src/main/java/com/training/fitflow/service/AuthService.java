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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final BruteForceProtectionService bruteForceProtectionService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;

    public String login(String username, String password) {
        log.info("Authenticating user with username={}", username);

        if (bruteForceProtectionService.isBlocked(username)) {
            log.warn("User is blocked due to brute force username={}", username);
            loginFailureCounter.increment();
            throw new UserBlockedException();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (DisabledException e) {
            log.warn("User is disabled username={}", username);
            loginFailureCounter.increment();
            throw new UserDeactivatedException(username);
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for username={}", username);
            bruteForceProtectionService.registerFailure(username);
            loginFailureCounter.increment();
            throw new BadCredentialException(username);
        }

        bruteForceProtectionService.resetAttempts(username);
        loginSuccessCounter.increment();

        String token = jwtTokenProvider.generateToken(username);
        log.info("Login successful, token generated for username={}", username);
        return token;
    }

    public void logout(String token) {
        log.info("Logout request received");
        tokenBlacklistService.blacklist(token);
        log.info("Token blacklisted successfully");
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for username={}", username);

        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();
            if (!passwordEncoder.matches(oldPassword, trainee.getPassword())) {
                log.warn("Invalid old password for username={}", username);
                throw new BadCredentialException(username);
            }
            trainee.setPassword(passwordEncoder.encode(newPassword));
            traineeRepository.save(trainee);
            log.info("Password changed for username={}", username);
            return;
        }

        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);
        if (trainerOpt.isPresent()) {
            Trainer trainer = trainerOpt.get();
            if (!passwordEncoder.matches(oldPassword, trainer.getPassword())) {
                log.warn("Invalid old password for username={}", username);
                throw new BadCredentialException(username);
            }
            trainer.setPassword(passwordEncoder.encode(newPassword));
            trainerRepository.save(trainer);
            log.info("Password changed for username={}", username);
            return;
        }

        throw new BadCredentialException(username);
    }
}
