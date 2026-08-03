package com.ageeva.accountservice.service;

import com.ageeva.accountservice.entity.account.Account;
import com.ageeva.accountservice.entity.transaction.*;
import com.ageeva.accountservice.exception.TransactionException;
import com.ageeva.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public List<Transaction> getAccountTransactions(UUID accountId) {
        log.debug("Getting transactions for account: {}", accountId);
        accountService.getAccount(accountId);
        Pageable pageable = Pageable.unpaged();
        Page<Transaction> page = transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
        return page.getContent();
    }

    public List<Transaction> getRecentTransactions(UUID accountId, int limit) {
        log.debug("Getting last {} transactions for account: {}", limit, accountId);
        accountService.getAccount(accountId);
        Pageable pageable = Pageable.ofSize(limit);
        Page<Transaction> page = transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
        return page.getContent();
    }


    public List<Transaction> getTransactionsByDateRange(UUID accountId, LocalDateTime from, LocalDateTime to) {
        log.debug("Getting transactions for account: {} from {} to {}", accountId, from, to);
        accountService.getAccount(accountId);
        if (from == null) {
            from = LocalDateTime.MIN;
        }
        if (to == null) {
            to = LocalDateTime.now();
        }
        return transactionRepository.findByAccountIdAndDateRange(accountId, from, to);
    }


    public Transaction getTransaction(UUID transactionId) {
        log.debug("Getting transaction: {}", transactionId);
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found: " + transactionId));
    }

    public Transaction getTransactionByReference(String transactionReference) {
        log.debug("Getting transaction by reference: {}", transactionReference);
        return transactionRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new TransactionException("Transaction not found: " + transactionReference));
    }

    public boolean existsByReference(String transactionReference) {
        return transactionRepository.existsByTransactionReference(transactionReference);
    }

    public BigDecimal getTotalDeposits(UUID accountId, LocalDateTime from, LocalDateTime to) {
        log.debug("Calculating total deposits for account: {} from {} to {}", accountId, from, to);
        List<Transaction> transactions = getTransactionsByDateRange(accountId, from, to);
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalWithdrawals(UUID accountId, LocalDateTime from, LocalDateTime to) {
        log.debug("Calculating total withdrawals for account: {} from {} to {}", accountId, from, to);
        List<Transaction> transactions = getTransactionsByDateRange(accountId, from, to);
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAWAL)
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTransactionCount(UUID accountId, LocalDateTime from, LocalDateTime to) {
        log.debug("Counting transactions for account: {} from {} to {}", accountId, from, to);
        List<Transaction> transactions = getTransactionsByDateRange(accountId, from, to);
        return transactions.size();
    }


    public String generateStatement(UUID accountId, LocalDateTime from, LocalDateTime to) {
        log.info("Generating statement for account: {} from {} to {}", accountId, from, to);
        Account account = accountService.getAccount(accountId);
        List<Transaction> transactions = getTransactionsByDateRange(accountId, from, to);
        if (transactions.isEmpty()) {
            return "No transactions found for the specified period.";
        }
        StringBuilder statement = new StringBuilder();
        statement.append("========================================\n");
        statement.append("          BANK STATEMENT                \n");
        statement.append("========================================\n");
        statement.append("Account: ").append(account.getAccountNumber()).append("\n");
        statement.append("Period: ").append(from).append(" - ").append(to).append("\n");
        statement.append("========================================\n\n");
        statement.append(String.format("%-20s %-15s %-10s %-10s\n",
                "Date", "Type", "Amount", "Balance After"));
        statement.append("----------------------------------------\n");
        for (Transaction t : transactions) {
            statement.append(String.format("%-20s %-15s %-10.2f %-10.2f\n",
                    t.getCreatedAt().toString(),
                    t.getType(),
                    t.getAmount(),
                    t.getBalanceAfter()));
        }
        statement.append("\n========================================\n");
        statement.append("Summary:\n");
        statement.append("  Total Deposits:    ").append(getTotalDeposits(accountId, from, to)).append("\n");
        statement.append("  Total Withdrawals: ").append(getTotalWithdrawals(accountId, from, to)).append("\n");
        statement.append("  Transaction Count: ").append(getTransactionCount(accountId, from, to)).append("\n");
        statement.append("  Current Balance:   ").append(account.getBalance()).append("\n");
        statement.append("========================================\n");
        return statement.toString();
    }

    public List<Transaction> getRelatedTransactions(UUID transactionId) {
        log.debug("Getting transactions related to: {}", transactionId);
        return transactionRepository.findByRelatedTransactionId(transactionId);
    }
}
