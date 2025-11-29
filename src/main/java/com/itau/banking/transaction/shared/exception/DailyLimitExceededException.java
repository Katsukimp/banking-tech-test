package com.itau.banking.transaction.shared.exception;

import java.math.BigDecimal;

public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(String message) {
        super(message);
    }
    
    public DailyLimitExceededException(BigDecimal dailyLimit, BigDecimal alreadyUsed, BigDecimal requestedAmount) {
        super(String.format("Limite diário excedido. Limite: R$ %.2f, Já utilizado: R$ %.2f, Solicitado: R$ %.2f", 
                dailyLimit, alreadyUsed, requestedAmount));
    }
}
