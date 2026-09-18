package com.greenpulse.api.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Picks OpenAI or Gemini based on `llm.provider`. If `llm.api-key` is blank, the
 * client is still created (so the app wires up cleanly) but ClassificationService /
 * PolicyQaService / TrendSummaryService check the same property themselves and fall
 * back to rule-based logic instead of ever calling it — so the whole pipeline runs
 * end-to-end with zero external calls until you add a real key.
 */
@Configuration
public class LlmClientConfig {

    @Value("${llm.provider:openai}")
    private String provider;

    @Value("${llm.api-key:}")
    private String apiKey;

    @Value("${llm.model:}")
    private String model;

    @Bean
    public RestTemplate llmRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
    }

    @Bean
    public LlmClient llmClient(RestTemplate llmRestTemplate) {
        if ("gemini".equalsIgnoreCase(provider)) {
            String geminiModel = (model == null || model.isBlank()) ? "gemini-1.5-flash" : model;
            return new GeminiClient(llmRestTemplate, apiKey, geminiModel);
        }
        String openAiModel = (model == null || model.isBlank()) ? "gpt-4o-mini" : model;
        return new OpenAiClient(llmRestTemplate, apiKey, openAiModel);
    }
}
