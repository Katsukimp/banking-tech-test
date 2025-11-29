package com.itau.banking.transaction.shared.exception;

public class BacenApiException extends RuntimeException {
    public BacenApiException(String message) {
        super(message);
    }
    
    public BacenApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
