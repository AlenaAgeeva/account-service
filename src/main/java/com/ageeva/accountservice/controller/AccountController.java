package com.ageeva.accountservice.controller;

import com.ageeva.accountservice.dto.request.BlockAccountRequest;
import com.ageeva.accountservice.dto.request.CreateAccountRequest;
import com.ageeva.accountservice.dto.request.DepositRequest;
import com.ageeva.accountservice.dto.request.WithdrawRequest;
import com.ageeva.accountservice.entity.account.Account;
import com.ageeva.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        log.info("API: Creating account for customer {}", request.getCustomerId());
        Account account = accountService.createAccount(
                request.getCustomerId(),
                request.getType(),
                request.getCurrency(),
                UUID.randomUUID().toString()
        );
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable UUID id) {
        log.info("API: Fetching account {}", id);
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable UUID id) {
        log.info("API: Fetching balance for account {}", id);
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestBody DepositRequest request) {
        log.info("API: Deposit {} to account {}", request.getAmount(), request.getAccountId());
        accountService.deposit(
                request.getAccountId(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody WithdrawRequest request) {
        log.info("API: Withdraw {} from account {}", request.getAmount(), request.getAccountId());
        accountService.withdraw(
                request.getAccountId(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockAccount(
            @PathVariable UUID id,
            @RequestBody BlockAccountRequest request
    ) {
        log.info("API: Blocking account {} reason={}", id, request.getReason());
        accountService.blockAccount(id, request.getReason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockAccount(@PathVariable UUID id) {
        log.info("API: Unblocking account {}", id);
        accountService.unblockAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Void> closeAccount(@PathVariable UUID id) {
        log.info("API: Closing account {}", id);
        accountService.closeAccount(id);
        return ResponseEntity.noContent().build();
    }
}
