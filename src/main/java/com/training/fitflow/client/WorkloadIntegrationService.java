package com.training.fitflow.client;

import com.training.fitflow.client.dto.TrainerWorkloadRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadIntegrationService {

    private final WorkloadClient workloadClient;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public void sendWorkloadUpdate(TrainerWorkloadRequest request) {
        workloadClient.updateWorkload(request);
        log.info("Workload update sent: trainer={}, action={}", request.trainerUsername(), request.actionType());
    }

    private void fallback(TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload service unavailable, skipping update for trainer={}, action={}. Reason: {}",
                request.trainerUsername(), request.actionType(), ex.getMessage());
    }
}