package com.training.fitflow.client;

import com.training.fitflow.client.dto.TrainerWorkloadRequest;
import com.training.fitflow.client.dto.TrainerWorkloadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "fitflow-workload-service")
public interface WorkloadClient {
    @PostMapping("/api/v1/workload")
    void updateWorkload(@RequestBody TrainerWorkloadRequest request);

    @GetMapping("/api/v1/workload/{username}")
    TrainerWorkloadResponse getWorkload(@PathVariable("username") String username,
                                        @RequestParam(value = "year", required = false) Integer year,
                                        @RequestParam(value = "month", required = false) Integer month);
}
