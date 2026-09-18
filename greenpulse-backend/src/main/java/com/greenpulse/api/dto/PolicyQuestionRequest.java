package com.greenpulse.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PolicyQuestionRequest {
    @NotBlank
    private String question;
}
