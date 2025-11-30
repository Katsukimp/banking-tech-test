package com.itau.banking.transaction.shared.exception;

public class SelfTransferException extends RuntimeException {
    public SelfTransferException() {
        super("Transferência para a mesma conta não é permitida.");
    }
}
