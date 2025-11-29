package com.itau.banking.transaction.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransferResponse(
        Long transactionId,
        String idempotencyKey,
        String status,
        AccountInfo sourceAccount,
        AccountInfo destinationAccount,
        BigDecimal amount,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime transactionDate,
        String message
) {
    public record AccountInfo(
            Long accountId,
            String accountNumber,
            String customerName
    ) {}
}