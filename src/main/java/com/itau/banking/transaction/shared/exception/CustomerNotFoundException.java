package com.itau.banking.transaction.shared.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(Long accountId) {
        super("Cliente não encontrado: " + accountId);
    }
}
