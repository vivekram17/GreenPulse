package com.greenpulse.api.service;

import com.greenpulse.api.dto.TrendSummaryResponse;
import com.greenpulse.api.llm.LlmClient;
import com.greenpulse.api.model.Complaint;
import com.greenpulse.api.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrendSummaryService {

    private static final Logger log = LoggerFactory.getLogger(TrendSummaryService.class);

    private static final String SYSTEM_PROMPT = """
            You are a municipal sustainability analyst. Given complaint counts by category and
            by location, write a short (3-4 sentence) plain-language summary for a city planner,
            calling out the biggest problem area and one concrete suggestion.
            """;

    private final ComplaintRepository repository;
    private final LlmClient llmClient;

    @Value("${llm.enabled:false}")
    private boolean llmEnabled;

    public TrendSummaryService(ComplaintRepository repository, LlmClient llmClient) {
        this.repository = repository;
        this.llmClient = llmClient;
    }

    public TrendSummaryResponse generateSummary() {
        List<Complaint> complaints = repository.findAll();

        Map<String, Long> byCategory = complaints.stream()
                .collect(Collectors.groupingBy(c -> c.getCategory().name(),
                        LinkedHashMap::new, Collectors.counting()));

        Map<String, Long> byLocation = complaints.stream()
                .filter(c -> c.getLocation() != null && !c.getLocation().isBlank()
                        && !"unspecified".equalsIgnoreCase(c.getLocation()))
                .collect(Collectors.groupingBy(Complaint::getLocation,
                        LinkedHashMap::new, Collectors.counting()));

        String narrative = llmEnabled
                ? summarizeWithLlm(byCategory, byLocation, complaints.size())
                : defaultNarrative(byCategory, complaints.size());

        return new TrendSummaryResponse(byCategory, byLocation, narrative);
    }

    private String summarizeWithLlm(Map<String, Long> byCategory, Map<String, Long> byLocation, long total) {
        try {
            String prompt = "Complaints by category: " + byCategory + "\nComplaints by location: " + byLocation;
            return llmClient.complete(SYSTEM_PROMPT, prompt).trim();
        } catch (Exception e) {
            log.warn("LLM trend summary failed, using default narrative: {}", e.getMessage());
            return defaultNarrative(byCategory, total);
        }
    }

    private String defaultNarrative(Map<String, Long> byCategory, long total) {
        if (total == 0) {
            return "No complaints logged yet.";
        }
        String topCategory = byCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
        return "Out of " + total + " complaints, " + topCategory
                + " is the most common category. Enable the LLM (llm.enabled=true) for a richer, AI-generated summary.";
    }
}