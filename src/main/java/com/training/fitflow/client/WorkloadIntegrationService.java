package com.training.fitflow.client;

import com.training.fitflow.client.dto.TrainerWorkloadRequest;
import com.training.fitflow.client.dto.TrainerWorkloadResponse;
import com.training.fitflow.exception.WorkloadServiceUnavailableException;
import feign.FeignException;
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
        log.info("Workload update sent: trainer={}, action={}",
                request.trainerUsername(), request.actionType());
    }

    private void sendWorkloadUpdateFallback(TrainerWorkloadRequest request, FeignException ex) {
        if (ex.status() >= 400 && ex.status() < 500) {
            log.error("Workload service rejected update: status={}, trainer={}, action={}",
                    ex.status(), request.trainerUsername(), request.actionType());
            throw ex;
        }
        log.error("Workload service error during update: status={}, trainer={}, action={}",
                ex.status(), request.trainerUsername(), request.actionType());
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
            String username, FeignException ex) {
        if (ex.status() >= 400 && ex.status() < 500) {
            log.warn("Workload service returned client error: status={}, trainer={}",
                    ex.status(), username);
            throw ex;
        }
        log.error("Workload service returned server error: status={}, trainer={}",
                ex.status(), username, ex);
        throw new WorkloadServiceUnavailableException(
                "Workload data temporarily unavailable, please try later");
    }

    private TrainerWorkloadResponse getWorkloadFallback(
            String username, Throwable ex) {
        log.error("Workload service unavailable for trainer={}", username, ex);
        throw new WorkloadServiceUnavailableException(
                "Workload data temporarily unavailable, please try later");
    }
}