package com.ageeva.accountservice.exception;

public class AccountBalanceException extends RuntimeException {
    public AccountBalanceException(String message) {
        super(message);
    }
}
