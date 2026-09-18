package com.greenpulse.api.dto;

import com.greenpulse.api.model.Complaint;
import com.greenpulse.api.model.ComplaintCategory;
import com.greenpulse.api.model.ComplaintStatus;
import com.greenpulse.api.model.Urgency;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ComplaintResponse {
    private Long id;
    private String description;
    private ComplaintCategory category;
    private Urgency urgency;
    private String location;
    private ComplaintStatus status;
    private String department;
    private String aiReasoning;
    private Instant createdAt;
    private Instant updatedAt;

    public static ComplaintResponse fromEntity(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId())
                .description(c.getDescription())
                .category(c.getCategory())
                .urgency(c.getUrgency())
                .location(c.getLocation())
                .status(c.getStatus())
                .department(c.getDepartment())
                .aiReasoning(c.getAiReasoning())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
