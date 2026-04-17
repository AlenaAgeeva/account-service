package com.ageeva.accountservice.dto.response;

import com.ageeva.accountservice.entity.account.AccountStatus;
import com.ageeva.accountservice.entity.account.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        AccountType type,
        AccountStatus status,
        BigDecimal balance,
        String currency,
        LocalDateTime createdAt) {
}
