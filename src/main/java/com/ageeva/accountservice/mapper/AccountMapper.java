package com.ageeva.accountservice.mapper;

import com.ageeva.accountservice.dto.request.CreateAccountRequest;
import com.ageeva.accountservice.dto.response.AccountResponse;
import com.ageeva.accountservice.dto.response.BalanceResponse;
import com.ageeva.accountservice.entity.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponse toResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "blockedAt", ignore = true)
    @Mapping(target = "blockedReason", ignore = true)
    @Mapping(target = "version", ignore = true)
    Account toEntity(CreateAccountRequest request);

    BalanceResponse toBalanceResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(@MappingTarget Account account, CreateAccountRequest request);
}
