package com.itau.banking.transaction.integration.bacen.dto;

import com.itau.banking.transaction.shared.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BacenNotificationRequest {
    
    private Long transactionId;
    private String idempotencyKey;
    
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    
    private BigDecimal amount;
    private TransactionType transactionType;
    
    private String customerName;
    private String customerCpf;
    
    private LocalDateTime transactionDate;
}
