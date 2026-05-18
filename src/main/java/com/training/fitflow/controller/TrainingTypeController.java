package com.training.fitflow.controller;

import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import com.training.fitflow.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping
    public ResponseEntity<List<SpecializationResponse>> getAll() {
        return ResponseEntity.ok().body(trainingTypeService.getAll());
    }
}
