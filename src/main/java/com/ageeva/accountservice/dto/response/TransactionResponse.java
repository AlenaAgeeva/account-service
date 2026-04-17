package com.ageeva.accountservice.dto.response;

import com.ageeva.accountservice.entity.transaction.TransactionStatus;
import com.ageeva.accountservice.entity.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String transactionReference,
        TransactionType type,
        BigDecimal amount,
        String currency,
        BigDecimal balanceAfter,
        String description,
        TransactionStatus status,
        LocalDateTime createdAt) {
}
