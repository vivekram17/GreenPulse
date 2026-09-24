package com.greenpulse.api.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Picks the LLM client based on `llm.provider`. When `llm.enabled=false`,
 * ClassificationService / PolicyQaService / TrendSummaryService skip calling
 * the client entirely and fall back to rule-based logic — so the whole
 * pipeline still runs end-to-end with zero external calls when disabled.
 */
@Configuration
public class LlmClientConfig {

    @Value("${llm.provider:ibm}")
    private String provider;

    @Value("${llm.api-key:}")
    private String apiKey;

    @Value("${llm.api-url:http://localhost:11434/api/chat}")
    private String apiUrl;

    @Value("${llm.model:}")
    private String model;

    @Bean
    public RestTemplate llmRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(200))
                .build();
    }

    @Bean
    public LlmClient llmClient(RestTemplate llmRestTemplate) {
        if ("ibm".equalsIgnoreCase(provider) || "ollama".equalsIgnoreCase(provider)) {
            String graniteModel = (model == null || model.isBlank()) ? "ibm/granite4.2:3b" : model;
            return new IbmGraniteClient(llmRestTemplate, apiUrl, graniteModel);
        }
        if ("gemini".equalsIgnoreCase(provider)) {
            String geminiModel = (model == null || model.isBlank()) ? "gemini-1.5-flash" : model;
            return new GeminiClient(llmRestTemplate, apiKey, geminiModel);
        }
        String openAiModel = (model == null || model.isBlank()) ? "gpt-4o-mini" : model;
        return new OpenAiClient(llmRestTemplate, apiKey, openAiModel);
    }
}