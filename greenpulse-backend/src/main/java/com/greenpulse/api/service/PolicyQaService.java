package com.greenpulse.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.api.dto.PolicyAnswerResponse;
import com.greenpulse.api.llm.LlmClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A deliberately small RAG pipeline: keyword-overlap retrieval instead of embeddings,
 * over a handful of local policy snippets instead of a vector database. Swap
 * `rankByKeywordOverlap` for an embeddings-based search once the policy set grows.
 */
@Service
public class PolicyQaService {

    private static final Logger log = LoggerFactory.getLogger(PolicyQaService.class);

    private static final String SYSTEM_PROMPT = """
            You are a municipal policy assistant. Answer the citizen's question using ONLY the
            provided policy snippets. If the snippets don't cover the question, say so plainly.
            Keep the answer to 2-3 sentences.
            """;

    private final LlmClient llmClient;
    private final List<PolicySnippet> snippets;

    @Value("${llm.api-key:}")
    private String apiKey;

    public PolicyQaService(LlmClient llmClient) throws Exception {
        this.llmClient = llmClient;
        this.snippets = loadSnippets();
    }

    public PolicyAnswerResponse ask(String question) {
        List<PolicySnippet> topMatches = rankByKeywordOverlap(question, snippets, 3);
        List<String> sources = topMatches.stream().map(PolicySnippet::title).toList();

        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String context = topMatches.stream()
                        .map(s -> "[" + s.title() + "] " + s.text())
                        .collect(Collectors.joining("\n\n"));
                String prompt = "Policy snippets:\n" + context + "\n\nQuestion: " + question;
                String answer = llmClient.complete(SYSTEM_PROMPT, prompt).trim();
                return new PolicyAnswerResponse(answer, sources);
            } catch (Exception e) {
                log.warn("LLM policy answer failed, returning best-matching snippet: {}", e.getMessage());
            }
        }
        String fallback = topMatches.isEmpty()
                ? "No matching policy found. Configure an LLM API key for a generated answer."
                : topMatches.get(0).text();
        return new PolicyAnswerResponse(fallback, sources);
    }

    private List<PolicySnippet> rankByKeywordOverlap(String question, List<PolicySnippet> all, int topK) {
        List<String> qWords = Arrays.asList(question.toLowerCase().split("\\W+"));
        return all.stream()
                .sorted(Comparator.comparingInt(
                        (PolicySnippet s) -> overlapScore(qWords, s.text().toLowerCase())).reversed())
                .limit(topK)
                .collect(Collectors.toList());
    }

    private int overlapScore(List<String> qWords, String text) {
        int score = 0;
        for (String w : qWords) {
            if (w.length() > 3 && text.contains(w)) score++;
        }
        return score;
    }

    private List<PolicySnippet> loadSnippets() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("policy-knowledge-base.json").getInputStream()) {
            JsonNode root = mapper.readTree(is);
            List<PolicySnippet> result = new ArrayList<>();
            for (JsonNode node : root) {
                result.add(new PolicySnippet(node.path("title").asText(), node.path("text").asText()));
            }
            return result;
        }
    }

    private record PolicySnippet(String title, String text) {
    }
}
