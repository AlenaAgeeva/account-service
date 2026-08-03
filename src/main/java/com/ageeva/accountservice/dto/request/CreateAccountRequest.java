package com.ageeva.accountservice.dto.request;

import com.ageeva.accountservice.entity.account.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateAccountRequest {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @Size(min = 3, max = 3, message = "Currency must be 3-letter code")
    private String currency = "USD";
}
