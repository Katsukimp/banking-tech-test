package com.itau.banking.transaction.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.sourceAccount.id = :accountId " +
            "AND DATE(t.transactionDate) = :date " +
            "AND t.status = 'COMPLETED'")

    BigDecimal sumDailyTransactionsByAccountId(
            @Param("accountId") Long accountId,
            @Param("date") LocalDate date
    );

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
