package com.greenpulse.api.dto;

import com.greenpulse.api.model.ComplaintCategory;
import com.greenpulse.api.model.Urgency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationResult {
    private ComplaintCategory category;
    private Urgency urgency;
    private String location;
    private String reasoning;
}
