package com.greenpulse.api.llm;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Talks to a local Ollama server running an IBM Granite model
 * (e.g. `ollama pull ibm/granite4.2:3b`). No API key required.
 */
public class IbmGraniteClient implements LlmClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String model;

    public IbmGraniteClient(RestTemplate restTemplate, String apiUrl, String model) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", model,
                "stream", false,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        JsonNode response = restTemplate.postForObject(apiUrl, request, JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("Empty response from Ollama/IBM Granite");
        }
        return response.path("message").path("content").asText();
    }
}