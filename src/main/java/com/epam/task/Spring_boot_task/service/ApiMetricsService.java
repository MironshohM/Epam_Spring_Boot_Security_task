package com.epam.task.Spring_boot_task.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ApiMetricsService {
    private final Counter requestCounter;

    public ApiMetricsService(MeterRegistry registry) {
        this.requestCounter = Counter.builder("api_requests_total")
                .description("Total API requests")
                .register(registry);
    }

    public void incrementCounter() {
        requestCounter.increment();
    }
}