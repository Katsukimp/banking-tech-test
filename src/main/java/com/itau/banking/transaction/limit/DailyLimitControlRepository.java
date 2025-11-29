package com.itau.banking.transaction.limit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyLimitControlRepository extends JpaRepository<DailyLimitControl, Long> {
    Optional<DailyLimitControl> findByAccountIdAndDate(Long accountId, LocalDate date);
}
