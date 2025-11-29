package com.itau.banking.transaction.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {

    private Long accountId;
    private String accountNumber;
    private String customerName;

    private BigDecimal balance;

    private BigDecimal dailyLimitUsed;
    private BigDecimal dailyLimitAvailable;
    private BigDecimal dailyLimitTotal;

    private String status;
}
