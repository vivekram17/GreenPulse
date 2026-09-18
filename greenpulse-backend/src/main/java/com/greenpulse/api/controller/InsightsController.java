package com.greenpulse.api.controller;

import com.greenpulse.api.dto.TrendSummaryResponse;
import com.greenpulse.api.service.TrendSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    private final TrendSummaryService trendSummaryService;

    public InsightsController(TrendSummaryService trendSummaryService) {
        this.trendSummaryService = trendSummaryService;
    }

    @GetMapping("/trends")
    public ResponseEntity<TrendSummaryResponse> trends() {
        return ResponseEntity.ok(trendSummaryService.generateSummary());
    }
}
