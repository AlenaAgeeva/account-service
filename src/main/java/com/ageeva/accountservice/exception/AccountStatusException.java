package com.ageeva.accountservice.exception;

public class AccountStatusException extends  RuntimeException {
    public AccountStatusException(String message){
        super(message);
    }
}
