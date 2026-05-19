package com.training.fitflow.controller;

import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.dto.training.response.TraineeTrainingResponse;
import com.training.fitflow.dto.training.response.TrainerTrainingResponse;
import com.training.fitflow.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Void> createTraining(@Valid @RequestBody TrainingCreateRequest request) {
        trainingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/trainee")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(
            @RequestParam("username") String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "trainerName", required = false) String trainerName,
            @RequestParam(name = "typeId", required = false) Long typeId
    ) {
        List<TraineeTrainingResponse> response = trainingService
                .getTraineeTrainings(username, fromDate, toDate, trainerName, typeId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/trainer")
    public ResponseEntity<List<TrainerTrainingResponse>> getTraineeTrainings(
            @RequestParam("username") String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "traineeName", required = false) String traineeName
    ) {
        List<TrainerTrainingResponse> response = trainingService
                .getTrainerTrainings(username, fromDate, toDate, traineeName);
        return ResponseEntity.ok().body(response);
    }
}
