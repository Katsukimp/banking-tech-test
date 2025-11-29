package com.itau.banking.transaction.transaction.validator;

import com.itau.banking.transaction.account.Account;
import java.math.BigDecimal;

public interface ValidationStrategy {
    void validate(Account source, Account destination, BigDecimal amount);
}
