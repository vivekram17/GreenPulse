package com.greenpulse.api.controller;

import com.greenpulse.api.dto.PolicyAnswerResponse;
import com.greenpulse.api.dto.PolicyQuestionRequest;
import com.greenpulse.api.service.PolicyQaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policy")
public class PolicyController {

    private final PolicyQaService policyQaService;

    public PolicyController(PolicyQaService policyQaService) {
        this.policyQaService = policyQaService;
    }

    @PostMapping("/ask")
    public ResponseEntity<PolicyAnswerResponse> ask(@Valid @RequestBody PolicyQuestionRequest request) {
        return ResponseEntity.ok(policyQaService.ask(request.getQuestion()));
    }
}
