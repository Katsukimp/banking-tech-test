package com.itau.banking.transaction.transaction.validator;

import com.itau.banking.transaction.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class ValidationStrategyFactory {
    private final List<ValidationStrategy> orderedValidators;

    @Autowired
    public ValidationStrategyFactory(List<ValidationStrategy> validators){
        this.orderedValidators = validators.stream()
                .sorted(Comparator.comparingInt(this::getOrder))
                .toList();
    }

    private int getOrder(ValidationStrategy validator) {
        ValidationOrder annotation = validator.getClass()
                .getAnnotation(ValidationOrder.class);
        return annotation != null ? annotation.value() : Integer.MAX_VALUE;
    }

    public void validateAll(Account source, Account destination, BigDecimal amount) {
        orderedValidators.forEach(v -> v.validate(source, destination, amount));
    }
}
