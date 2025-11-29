package com.itau.banking.transaction.shared.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String message) {
        super(message);
    }
    
    public DuplicateTransactionException(String idempotencyKey, Long existingTransactionId) {
        super(String.format("Transação duplicada. Idempotency Key: %s, Transação existente: %d", 
                idempotencyKey, existingTransactionId));
    }
}
