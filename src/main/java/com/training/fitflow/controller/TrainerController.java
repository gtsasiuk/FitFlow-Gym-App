package com.training.fitflow.controller;

import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.request.TrainerUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.dto.trainer.response.TrainerProfileResponse;
import com.training.fitflow.dto.trainer.response.TrainerUpdateResponse;
import com.training.fitflow.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    public ResponseEntity<TrainerCreateResponse> createTrainer(@Valid @RequestBody TrainerCreateRequest request) {
        TrainerCreateResponse response = trainerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable("username") String username) {
        TrainerProfileResponse response = trainerService.getByUsername(username);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> getTrainerProfile(@PathVariable("username") String username,
                                                                   @Valid @RequestBody TrainerUpdateRequest request) {
        TrainerUpdateResponse response = trainerService.update(username, request);
        return ResponseEntity.ok().body(response);
    }
}
