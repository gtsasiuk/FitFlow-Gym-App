package com.training.fitflow.controller;

import com.training.fitflow.dto.common.request.UserStatusUpdateRequest;
import com.training.fitflow.dto.trainee.request.TraineeCreateRequest;
import com.training.fitflow.dto.trainee.request.TraineeUpdateRequest;
import com.training.fitflow.dto.trainee.response.TraineeCreateResponse;
import com.training.fitflow.dto.trainee.response.TraineeProfileResponse;
import com.training.fitflow.dto.trainee.response.TraineeUpdateResponse;
import com.training.fitflow.dto.trainer.request.TraineeTrainersUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerSummaryResponse;
import com.training.fitflow.service.TraineeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
@Validated
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    public ResponseEntity<TraineeCreateResponse> createTrainee(@Valid @RequestBody TraineeCreateRequest request) {
        TraineeCreateResponse response = traineeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable("username")
                                                                    @NotBlank(message = "Username is required") String username) {
        TraineeProfileResponse response = traineeService.getByUsername(username);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> getUnassignedTrainers(@PathVariable("username")
                                                                              @NotBlank(message = "Username is required")
                                                                              String username) {
        List<TrainerSummaryResponse> response = traineeService.getUnassignedTrainers(username);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("username")
                                             @NotBlank(message = "Username is required") String username,
                                             @Valid @RequestBody UserStatusUpdateRequest request) {
        if (request.isActive()) {
            traineeService.activate(username);
        } else {
            traineeService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable("username")
                                                                      @NotBlank(message = "Username is required")
                                                                      String username,
                                                                      @Valid @RequestBody TraineeUpdateRequest request) {
        TraineeUpdateResponse response = traineeService.update(username, request);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> updateTraineeProfile(@PathVariable("username")
                                                                             @NotBlank(message = "Username is required")
                                                                             String username,
                                                                             @Valid @RequestBody TraineeTrainersUpdateRequest request) {
        List<TrainerSummaryResponse> response = traineeService.updateTraineeTrainers(username, request);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable("username")
                                              @NotBlank(message = "Username is required") String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
