package com.training.fitflow.client;

import com.training.fitflow.client.dto.TrainerWorkloadRequest;
import com.training.fitflow.client.dto.TrainerWorkloadResponse;
import com.training.fitflow.exception.WorkloadServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadIntegrationService {
    private final WorkloadClient workloadClient;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "sendWorkloadUpdateFallback")
    public void sendWorkloadUpdate(TrainerWorkloadRequest request) {
        workloadClient.updateWorkload(request);
        log.info("Workload update sent: trainer={}, action={}", request.trainerUsername(), request.actionType());
    }

    private void sendWorkloadUpdateFallback(TrainerWorkloadRequest request, Throwable ex) {
        log.error("Workload service unavailable, skipping update for trainer={}, action={}. Reason: {}",
                request.trainerUsername(), request.actionType(), ex.getMessage());
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "getWorkloadFallback")
    public TrainerWorkloadResponse getWorkload(String username, Integer year, Integer month) {
        return workloadClient.getWorkload(username, year, month);
    }

    private TrainerWorkloadResponse getWorkloadFallback(
            String username, Integer year, Integer month, Throwable ex) {
        log.error("Workload service unavailable for trainer={}", username, ex);
        throw new WorkloadServiceUnavailableException("Workload data temporarily unavailable, please try later");
    }
}