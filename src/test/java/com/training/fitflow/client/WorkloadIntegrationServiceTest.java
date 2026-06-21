package com.training.fitflow.client;

import com.training.fitflow.client.dto.TrainerWorkloadRequest;
import com.training.fitflow.client.dto.TrainerWorkloadResponse;
import com.training.fitflow.exception.WorkloadServiceUnavailableException;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkloadIntegrationService Tests")
class WorkloadIntegrationServiceTest {
    @Mock
    private WorkloadClient workloadClient;

    @InjectMocks
    private WorkloadIntegrationService service;

    private TrainerWorkloadRequest request;

    @BeforeEach
    void setUp() {
        request = new TrainerWorkloadRequest(
                "trainer1",
                "John",
                "Doe",
                true,
                LocalDate.of(2025, 1, 1),
                60L,
                TrainerWorkloadRequest.ActionType.ADD
        );
    }

    @Test
    @DisplayName("SendWorkloadUpdate → should call workload client")
    void sendWorkloadUpdate_shouldCallClient() {
        service.sendWorkloadUpdate(request);

        verify(workloadClient).updateWorkload(request);
    }

    @Test
    @DisplayName("GetWorkload → should return workload response")
    void getWorkload_shouldReturnResponse() {
        TrainerWorkloadResponse response = mock(TrainerWorkloadResponse.class);

        when(workloadClient.getWorkload("trainer1", 2025, 1)).thenReturn(response);

        TrainerWorkloadResponse result = service.getWorkload("trainer1", 2025, 1);

        assertNotNull(result);
        assertEquals(response, result);

        verify(workloadClient).getWorkload("trainer1", 2025, 1);
    }

    @Test
    @DisplayName("Feign fallback → should rethrow 4xx exception")
    void sendWorkloadUpdateFallback_shouldRethrow4xx() throws Exception {
        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(404);

        Method method = WorkloadIntegrationService.class
                .getDeclaredMethod(
                        "sendWorkloadUpdateFallback",
                        TrainerWorkloadRequest.class,
                        FeignException.class
                );

        method.setAccessible(true);
        assertThrows(Exception.class, () -> method.invoke(service, request, ex));
    }

    @Test
    @DisplayName("Feign fallback → should ignore 5xx exception")
    void sendWorkloadUpdateFallback_shouldIgnore5xx() throws Exception {
        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(500);

        Method method = WorkloadIntegrationService.class
                .getDeclaredMethod(
                        "sendWorkloadUpdateFallback",
                        TrainerWorkloadRequest.class,
                        FeignException.class
                );

        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(service, request, ex));
    }

    @Test
    @DisplayName("Throwable fallback → should not throw")
    void sendWorkloadUpdateFallback_shouldHandleThrowable() throws Exception {
        Method method = WorkloadIntegrationService.class
                .getDeclaredMethod(
                        "sendWorkloadUpdateFallback",
                        TrainerWorkloadRequest.class,
                        Throwable.class
                );

        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(service, request, new RuntimeException("service down")));
    }

    @Test
    @DisplayName("GetWorkloadFallback → should throw custom exception")
    void getWorkloadFallback_shouldThrowCustomException() throws Exception {
        Method method = WorkloadIntegrationService.class.getDeclaredMethod(
                        "getWorkloadFallback",
                        String.class,
                        Throwable.class
                );

        method.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> method.invoke(
                        service,
                        "trainer1",
                        new RuntimeException("down")
                ));

        assertTrue(ex.getCause() instanceof WorkloadServiceUnavailableException);
    }
}