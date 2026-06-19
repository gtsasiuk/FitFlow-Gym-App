package com.training.fitflow.security;

import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.training.fitflow.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username={}", username);

        return traineeRepository.findByUsername(username)
                .<UserDetails>map(this::toUserDetails)
                .or(() -> trainerRepository.findByUsername(username)
                        .map(this::toUserDetails))
                .orElseThrow(() -> {
                    log.warn("User not found username={}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.getActive())
                .accountLocked(false)
                .roles("USER")
                .build();
    }
}
