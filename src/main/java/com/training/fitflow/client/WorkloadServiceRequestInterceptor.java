package com.training.fitflow.client;

import com.training.fitflow.security.service.ServiceTokenProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkloadServiceRequestInterceptor implements RequestInterceptor {
    private final ServiceTokenProvider serviceTokenProvider;

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + serviceTokenProvider.generateToken());

        String transactionId = MDC.get("transactionId");
        if (transactionId != null) {
            template.header("X-Transaction-Id", transactionId);
        }
    }
}
