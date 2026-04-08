package com.ageeva.accountservice.entity.account;

public enum AccountType {
    CHECKING("Deposit account"),
    SAVINGS("Saving account"),
    BUSINESS("Business account");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
