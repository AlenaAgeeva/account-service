package com.ageeva.accountservice.entity.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false, length = 100)
    private String transactionReference;
    @Column(nullable = false)
    private UUID accountId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private UUID relatedTransactionId;
    @Column(length = 500)
    private String failureReason;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
