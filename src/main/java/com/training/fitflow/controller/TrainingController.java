package com.training.fitflow.controller;

import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Void> createTraining(@Valid @RequestBody TrainingCreateRequest request) {
        trainingService.create(request);
        return ResponseEntity.ok().build();
    }
}
