package com.training.fitflow.client;

import com.training.fitflow.client.dto.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "fitflow-workload-service")
public interface WorkloadClient {
    @PostMapping("/api/v1/workload")
    void updateWorkload(@RequestBody TrainerWorkloadRequest request);
}
