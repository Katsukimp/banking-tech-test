package com.itau.banking.transaction.shared.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
    
    public AccountNotFoundException(Long accountId) {
        super("Conta não encontrada: " + accountId);
    }
}
