package com.itau.banking.transaction.transaction.validator.concrete;

import com.itau.banking.transaction.account.Account;
import com.itau.banking.transaction.transaction.validator.ValidationOrder;
import com.itau.banking.transaction.transaction.validator.ValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@ValidationOrder(4)
public class SufficientBalanceValidationStrategy implements ValidationStrategy {

    @Override
    public void validate(Account source, Account destination, BigDecimal amount) {
        log.info("[SufficientBalanceValidator].[doValidate] - Validando saldo suficiente na conta de origem - Conta: {} - Saldo: {} - Valor da Transferência: {}",
                source.getId(), source.getBalance(), amount);

        if(source.getBalance().compareTo(amount) < 0){
            log.error("[SufficientBalanceValidator].[doValidate] - Saldo insuficiente na conta de origem - Conta: {} - Saldo: {} - Valor da Transferência: {}",
                    source.getId(), source.getBalance(), amount);
            throw new com.itau.banking.transaction.shared.exception.InsufficientBalanceException(source.getBalance(), amount);
        }

        log.info("[SufficientBalanceValidator].[doValidate] - Saldo suficiente validado com sucesso na conta de origem");
    }
}
