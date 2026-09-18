package com.greenpulse.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendSummaryResponse {
    private Map<String, Long> countsByCategory;
    private Map<String, Long> countsByLocation;
    private String narrativeSummary;
}
