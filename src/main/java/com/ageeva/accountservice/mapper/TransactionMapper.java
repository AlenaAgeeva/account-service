package com.ageeva.accountservice.mapper;

import com.ageeva.accountservice.dto.response.TransactionResponse;
import com.ageeva.accountservice.entity.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);
}
