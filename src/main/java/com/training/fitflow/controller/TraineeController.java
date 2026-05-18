package com.training.fitflow.controller;

import com.training.fitflow.dto.trainee.request.TraineeCreateRequest;
import com.training.fitflow.dto.trainee.request.TraineeUpdateRequest;
import com.training.fitflow.dto.trainee.response.TraineeCreateResponse;
import com.training.fitflow.dto.trainee.response.TraineeProfileResponse;
import com.training.fitflow.dto.trainee.response.TraineeUpdateResponse;
import com.training.fitflow.service.TraineeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    public ResponseEntity<TraineeCreateResponse> createTrainee(@Valid @RequestBody TraineeCreateRequest request) {
        TraineeCreateResponse response = traineeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable("username") String username) {
        TraineeProfileResponse response = traineeService.getByUsername(username);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable("username") String username,
            @Valid @RequestBody TraineeUpdateRequest request) {
        TraineeUpdateResponse response = traineeService.update(username, request);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable("username") String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
