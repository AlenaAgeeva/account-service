package com.ageeva.accountservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlockAccountRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
