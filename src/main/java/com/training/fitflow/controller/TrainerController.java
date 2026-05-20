package com.training.fitflow.controller;

import com.training.fitflow.dto.common.request.UserStatusUpdateRequest;
import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.request.TrainerUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.dto.trainer.response.TrainerProfileResponse;
import com.training.fitflow.dto.trainer.response.TrainerUpdateResponse;
import com.training.fitflow.service.TrainerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Validated
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    public ResponseEntity<TrainerCreateResponse> createTrainer(@Valid @RequestBody TrainerCreateRequest request) {
        TrainerCreateResponse response = trainerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable("username")
                                                                    @NotBlank(message = "Username is required") String username) {
        TrainerProfileResponse response = trainerService.getByUsername(username);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("username")
                                             @NotBlank(message = "Username is required") String username,
                                             @Valid @RequestBody UserStatusUpdateRequest request) {
        if (request.isActive()) {
            trainerService.activate(username);
        } else {
            trainerService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable("username")
                                                                   @NotBlank(message = "Username is required") String username,
                                                                   @Valid @RequestBody TrainerUpdateRequest request) {
        TrainerUpdateResponse response = trainerService.update(username, request);
        return ResponseEntity.ok().body(response);
    }
}
