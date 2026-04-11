package com.ageeva.accountservice.service;

import com.ageeva.accountservice.entity.account.Account;
import com.ageeva.accountservice.entity.account.AccountStatus;
import com.ageeva.accountservice.entity.account.AccountType;
import com.ageeva.accountservice.entity.transaction.Transaction;
import com.ageeva.accountservice.entity.transaction.TransactionStatus;
import com.ageeva.accountservice.entity.transaction.TransactionType;
import com.ageeva.accountservice.exception.AccountException;
import com.ageeva.accountservice.repository.AccountRepository;

import com.ageeva.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Account createAccount(UUID customerId, AccountType type, String currency, String accountNumber) {
        log.info("Creating new account for customer: {}, type: {}", customerId, type);
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountException("Account number already exists: " + accountNumber);
        }
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .customerId(customerId)
                .type(type)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .currency(currency != null ? currency : "USD")
                .build();
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully: {}", savedAccount.getAccountNumber());
        return savedAccount;
    }

    @Transactional(readOnly = true)
    public Account getAccount(UUID accountId) {
        log.debug("Fetching account: {}", accountId);
        return accountRepository
                .findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found: " + accountId));
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID accountId) {
        Account account = getAccount(accountId);
        return account.getBalance();

    }

    @Transactional
    public void deposit(UUID accountId, BigDecimal amount, String description) {
        log.info("Depositing {} to account: {}", amount, accountId);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountException("Deposit amount must be positive");
        }
        int updated = accountRepository.deposit(accountId, amount);
        if (updated == 0) {
            throw new AccountException("Account not found or not active: " + accountId);
        }
        Account account = getAccount(accountId);
        createAndSaveTransaction(accountId, TransactionType.DEPOSIT, amount,
                account.getBalance(), description, TransactionStatus.COMPLETED);

        log.info("Deposit completed. New balance: {}", account.getBalance());
    }

    @Retryable(
            value = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public void withdraw(UUID accountId, BigDecimal amount, String description) {
        log.info("Withdrawing {} from account: {}", amount, accountId);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountException("Withdrawal amount must be positive");
        }
        Account account = getAccount(accountId);
        BigDecimal oldBalance = account.getBalance();
        account.withdraw(amount);
        accountRepository.save(account);
        createAndSaveTransaction(accountId, TransactionType.WITHDRAWAL, amount.negate(),
                account.getBalance(), description, TransactionStatus.COMPLETED);
        log.info("Withdrawal completed. Old balance: {}, New balance: {}", oldBalance, account.getBalance());
    }

    @Transactional
    public void blockAccount(UUID accountId, String reason) {
        log.info("Blocking account: {}, reason: {}", accountId, reason);

        Account account = getAccount(accountId);
        account.block(reason);
        accountRepository.save(account);

        log.info("Account blocked successfully: {}", accountId);
    }

    @Transactional
    public void unblockAccount(UUID accountId) {
        log.info("Unblocking account: {}", accountId);

        Account account = getAccount(accountId);
        account.unblock();
        accountRepository.save(account);

        log.info("Account unblocked successfully: {}", accountId);
    }

    @Transactional
    public void closeAccount(UUID accountId) {
        log.info("Closing account: {}", accountId);
        Account account = getAccount(accountId);
        account.close();
        log.info("Account closed successfully: {}", accountId);
    }

    private void createAndSaveTransaction(UUID accountId, TransactionType type,
                                          BigDecimal amount, BigDecimal balanceAfter,
                                          String description, TransactionStatus status) {
        Transaction transaction = Transaction.builder()
                .transactionReference(generateTransactionReference())
                .accountId(accountId)
                .type(type)
                .amount(amount)
                .currency("USD")
                .balanceAfter(balanceAfter)
                .description(description)
                .status(status)
                .build();
        transactionRepository.save(transaction);
    }

    private String generateTransactionReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
