package com.itau.banking.transaction.shared.exception;

public class MinimumAmountException extends RuntimeException {
    public MinimumAmountException(String message) {
        super(message);
    }
}
