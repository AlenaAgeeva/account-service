package com.ageeva.accountservice.entity.account;

public enum AccountStatus {
    ACTIVE("Active"),
    BLOCKED("Blocked"),
    CLOSED("Closed");
    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
