package com.greenpulse.api.llm;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class GeminiClient implements LlmClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String model;

    public GeminiClient(RestTemplate restTemplate, String apiKey, String model) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String combinedPrompt = systemPrompt + "\n\n" + userPrompt;
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", combinedPrompt)))
                )
        );

        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        JsonNode response = restTemplate.postForObject(url, request, JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("Empty response from Gemini");
        }
        return response.path("candidates").path(0).path("content")
                .path("parts").path(0).path("text").asText();
    }
}
