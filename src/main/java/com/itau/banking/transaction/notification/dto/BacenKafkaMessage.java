package com.itau.banking.transaction.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BacenKafkaMessage {
    
    private Long transactionId;
    private Long notificationId;
    private String idempotencyKey;
    private Long sourceAccountId;
    private String sourceAccountNumber;
    private Long destinationAccountId;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private String customerName;
    private String customerCpf;
    private LocalDateTime transactionDate;
    private Integer retryCount;
}
