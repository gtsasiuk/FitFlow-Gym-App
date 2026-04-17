package com.training.fitflow.storage;

import com.training.fitflow.model.Trainee;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class InMemoryStorage {
    private final Map<Long, Trainee> trainees = new HashMap<>();
    private final Map<Long, Object> trainers = new HashMap<>();
    private final Map<Long, Object> trainings = new HashMap<>();
}
