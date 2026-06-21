package com.training.fitflow.client;

import com.training.fitflow.security.service.ServiceTokenProvider;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("WorkloadServiceRequestInterceptor Tests")
class WorkloadServiceRequestInterceptorTest {
    private ServiceTokenProvider serviceTokenProvider;
    private WorkloadServiceRequestInterceptor interceptor;

    @BeforeEach
    void setUp() {
        serviceTokenProvider = mock(ServiceTokenProvider.class);

        interceptor = new WorkloadServiceRequestInterceptor(serviceTokenProvider);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Apply → should add authorization header")
    void apply_shouldAddAuthorizationHeader() {
        when(serviceTokenProvider.generateToken()).thenReturn("jwt-token");

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertTrue(template.headers().get("Authorization").contains("Bearer jwt-token"));
        verify(serviceTokenProvider).generateToken();
    }

    @Test
    @DisplayName("Apply → should add transaction id header")
    void apply_shouldAddTransactionIdHeader() {
        when(serviceTokenProvider.generateToken()).thenReturn("jwt-token");

        MDC.put("transactionId", "tx-123");

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertTrue(template.headers().get("X-Transaction-Id").contains("tx-123"));
    }

    @Test
    @DisplayName("Apply → should skip transaction header when absent")
    void apply_shouldSkipTransactionHeader() {
        when(serviceTokenProvider.generateToken()).thenReturn("jwt-token");

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertFalse(template.headers().containsKey("X-Transaction-Id"));
    }
}