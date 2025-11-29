package com.itau.banking.transaction.shared.exception;

public class BacenRateLimitException extends BacenApiException {
    public BacenRateLimitException(String message) {
        super(message);
    }
}
