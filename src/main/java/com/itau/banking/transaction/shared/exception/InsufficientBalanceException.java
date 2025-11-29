package com.itau.banking.transaction.shared.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(BigDecimal balance, BigDecimal amount) {
        super(String.format("Saldo insuficiente. Saldo atual: R$ %.2f, Valor solicitado: R$ %.2f", 
                balance, amount));
    }
}
