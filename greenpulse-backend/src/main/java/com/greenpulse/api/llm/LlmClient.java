package com.greenpulse.api.llm;

public interface LlmClient {

    /**
     * Sends a system + user prompt to the underlying LLM and returns the raw text response.
     */
    String complete(String systemPrompt, String userPrompt);
}
