package com.greenpulse.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComplaintSubmitRequest {

    @NotBlank(message = "description is required")
    private String description;

    /** Optional URL to a photo the citizen attached. */
    private String photoUrl;

    /** Optional — used if the AI can't extract a location from the text itself. */
    private String locationHint;
}
