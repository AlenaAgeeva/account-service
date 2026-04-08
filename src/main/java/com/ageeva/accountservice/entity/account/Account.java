package com.ageeva.accountservice.entity.account;

import com.ageeva.accountservice.exception.AccountBalanceException;
import com.ageeva.accountservice.exception.AccountStatusException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false, length = 50)
    private String accountNumber;
    @Column(nullable = false)
    private UUID customerId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false, length = 3)
    private String currency;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime blockedAt;
    @Column(length = 255)
    private String blockedReason;
    @Version
    private Long version;

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountBalanceException("Deposit amount must be a positive number.");
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new AccountStatusException("Account status must be active.");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountBalanceException("Withdrawal  amount must be a positive number.");
        }
        if (!isActive()) {
            throw new AccountStatusException("Account status must be active.");
        }
        if (!hasSufficientFunds(amount)) {
            throw new AccountBalanceException(
                    String.format("Insufficient funds. Balance: %s, Requested: %s", this.balance, amount));
        }
        this.balance = this.balance.subtract(amount);
    }

    public void block(String reason) {
        if (isBlocked()) {
            throw new AccountStatusException("Account is already blocked.");
        }
        if (isClosed()) {
            throw new AccountStatusException("Cannot block closed account");
        }
        this.status = AccountStatus.BLOCKED;
        this.blockedAt = LocalDateTime.now();
        this.blockedReason = reason;
    }

    public void unblock() {
        if (!isBlocked()) {
            throw new AccountStatusException("Account is not blocked.");
        }
        this.status = AccountStatus.ACTIVE;
        this.blockedAt = null;
        this.blockedReason = null;
    }

    public void close() {
        if (isClosed()) {
            throw new AccountStatusException("Account is already closed.");
        }
        if (this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountBalanceException("Cannot close account with non-zero balance: " + this.balance);
        }
        this.status = AccountStatus.CLOSED;
    }

    private boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    private boolean isBlocked() {
        return this.status == AccountStatus.BLOCKED;
    }

    private boolean isClosed() {
        return this.status == AccountStatus.CLOSED;
    }

    private boolean hasSufficientFunds(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
}
