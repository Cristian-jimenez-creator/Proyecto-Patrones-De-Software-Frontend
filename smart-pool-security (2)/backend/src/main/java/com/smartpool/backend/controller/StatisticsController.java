package com.smartpool.backend.controller;

import com.smartpool.backend.service.StatisticsService;
import com.smartpool.backend.service.StatisticsService.Statistics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService service;

    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @GetMapping
    public Statistics get() {
        return service.compute();
    }
}
