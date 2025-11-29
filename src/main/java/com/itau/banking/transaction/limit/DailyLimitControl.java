package com.itau.banking.transaction.limit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "daily_limit_control",
        uniqueConstraints = @UniqueConstraint(columnNames = {"accountId", "date"}),
        indexes = @Index(name = "idx_limit_account_date", columnList = "accountId, date")
)
@EntityListeners(AuditingEntityListener.class)
public class DailyLimitControl {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer transactionCount = 0;

    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
}
