package com.greenpulse.api.service;

import com.greenpulse.api.dto.ClassificationResult;
import com.greenpulse.api.llm.LlmClient;
import com.greenpulse.api.model.ComplaintCategory;
import com.greenpulse.api.model.Urgency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * These tests never set llm.api-key, so ClassificationService always takes the
 * rule-based fallback path - no network calls, no API key required to run them.
 */
class ClassificationServiceTest {

    private final ClassificationService service = new ClassificationService(
            // never invoked in these tests since no API key is configured
            (systemPrompt, userPrompt) -> {
                throw new UnsupportedOperationException("LLM should not be called in fallback mode");
            }
    );

    @Test
    void classifiesWaterComplaintsByKeyword() {
        ClassificationResult result = service.classify("There is a water pipe leaking near the main road.");
        assertEquals(ComplaintCategory.WATER, result.getCategory());
    }

    @Test
    void classifiesWasteComplaintsByKeyword() {
        ClassificationResult result = service.classify("Garbage has been piling up and not collected for a week.");
        assertEquals(ComplaintCategory.WASTE, result.getCategory());
    }

    @Test
    void flagsUrgentLanguageAsHighUrgency() {
        ClassificationResult result = service.classify("Emergency! Sewage overflow flooding the street.");
        assertEquals(Urgency.HIGH, result.getUrgency());
    }

    @Test
    void defaultsToOtherWhenNoKeywordMatches() {
        ClassificationResult result = service.classify("Something odd happened near my building yesterday.");
        assertEquals(ComplaintCategory.OTHER, result.getCategory());
    }
}
