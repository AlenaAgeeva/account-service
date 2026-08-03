package com.ageeva.accountservice.repository;

import com.ageeva.accountservice.entity.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.createdAt BETWEEN :start " +
            "AND :end ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountIdAndDateRange(
            @Param("accountId") UUID accountId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    boolean existsByTransactionReference(String transactionReference);

    List<Transaction> findByRelatedTransactionId(UUID relatedTransactionId);

    Optional<Transaction> findByTransactionReference(String transactionReference);
}
