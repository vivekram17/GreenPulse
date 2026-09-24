package com.greenpulse.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.api.dto.ClassificationResult;
import com.greenpulse.api.llm.LlmClient;
import com.greenpulse.api.model.ComplaintCategory;
import com.greenpulse.api.model.Urgency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClassificationService {

    private static final Logger log = LoggerFactory.getLogger(ClassificationService.class);

    private static final String SYSTEM_PROMPT = """
            You are a classification assistant for a municipal environmental grievance system.
            Given a citizen complaint, respond with ONLY a JSON object, no other text, with keys:
            "category" (one of WATER, WASTE, AIR, GREEN_COVER, ENERGY, OTHER),
            "urgency" (one of LOW, MEDIUM, HIGH),
            "location" (a short place name mentioned in the complaint, or "unspecified"),
            "reasoning" (one short sentence explaining the classification).
            """;

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${llm.enabled:false}")
    private boolean llmEnabled;

    public ClassificationService(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    public ClassificationResult classify(String complaintText) {
        if (llmEnabled) {
            try {
                return classifyWithLlm(complaintText);
            } catch (Exception e) {
                log.warn("LLM classification failed, falling back to rule-based classifier: {}", e.getMessage());
            }
        }
        return ruleBasedClassify(complaintText);
    }

    private ClassificationResult classifyWithLlm(String complaintText) throws Exception {
        String raw = llmClient.complete(SYSTEM_PROMPT, complaintText);
        String json = extractJson(raw);
        JsonNode node = objectMapper.readTree(json);

        ComplaintCategory category = parseCategory(node.path("category").asText("OTHER"));
        Urgency urgency = parseUrgency(node.path("urgency").asText("MEDIUM"));
        String location = node.path("location").asText("unspecified");
        String reasoning = node.path("reasoning").asText("Classified by AI model.");

        return new ClassificationResult(category, urgency, location, reasoning);
    }

    /** Keyword-based fallback used when the LLM is disabled, or the LLM call fails. */
    private ClassificationResult ruleBasedClassify(String text) {
        String t = text.toLowerCase();
        ComplaintCategory category = ComplaintCategory.OTHER;
        Urgency urgency = Urgency.MEDIUM;

        if (containsAny(t, "water", "leak", "pipe", "sewage", "drain")) {
            category = ComplaintCategory.WATER;
        } else if (containsAny(t, "garbage", "trash", "waste", "dump", "litter")) {
            category = ComplaintCategory.WASTE;
        } else if (containsAny(t, "smoke", "air", "pollution", "smell", "fumes")) {
            category = ComplaintCategory.AIR;
        } else if (containsAny(t, "tree", "park", "green", "forest")) {
            category = ComplaintCategory.GREEN_COVER;
        } else if (containsAny(t, "power", "electric", "streetlight", "energy")) {
            category = ComplaintCategory.ENERGY;
        }

        if (containsAny(t, "urgent", "emergency", "overflow", "fire", "danger")) {
            urgency = Urgency.HIGH;
        } else if (containsAny(t, "minor", "small", "occasional")) {
            urgency = Urgency.LOW;
        }

        return new ClassificationResult(category, urgency, "unspecified",
                "Classified by keyword-matching fallback (LLM disabled).");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }

    private ComplaintCategory parseCategory(String value) {
        try {
            return ComplaintCategory.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            return ComplaintCategory.OTHER;
        }
    }

    private Urgency parseUrgency(String value) {
        try {
            return Urgency.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            return Urgency.MEDIUM;
        }
    }

    /** LLMs sometimes wrap JSON in prose or code fences - pull out the {...} block. */
    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }
        return raw;
    }
}