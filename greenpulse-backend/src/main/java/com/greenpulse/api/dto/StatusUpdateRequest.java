package com.greenpulse.api.dto;

import com.greenpulse.api.model.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull
    private ComplaintStatus status;
}
