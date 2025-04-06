package com.epam.task.Spring_boot_task.controller;

import com.epam.task.Spring_boot_task.service.ApiMetricsService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
@RequestMapping("/api")
public class ApiController {
    private final ApiMetricsService apiMetricsService;

    public ApiController(ApiMetricsService apiMetricsService) {
        this.apiMetricsService = apiMetricsService;
    }

    @GetMapping("/data")
    public String getData() {
        apiMetricsService.incrementCounter();
        return "API Response";
    }
}