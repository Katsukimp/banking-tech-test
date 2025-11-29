package com.itau.banking.transaction.transaction.validator.concrete;

import com.itau.banking.transaction.account.Account;
import com.itau.banking.transaction.shared.exception.InactiveAccountException;
import com.itau.banking.transaction.transaction.validator.ValidationOrder;
import com.itau.banking.transaction.transaction.validator.ValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@ValidationOrder(1)
public class AccountActiveValidationStrategy implements ValidationStrategy {

    @Override
    public void validate(Account source, Account destination, BigDecimal amount) {
        log.info("[AccountActiveValidationStrategy].[validate] - Validando se contas estão ativas - Conta Origem: {} - Conta Destino: {}",
                source.getId(), destination.getId());

        if(source.isInactive()){
            log.error("[AccountActiveValidationStrategy].[validate] - Conta de origem inativa: {}", source.getId());
            throw new InactiveAccountException("Conta de origem inativa: " + source.getId());
        }

        if(destination.isInactive()){
            log.error("[AccountActiveValidationStrategy].[validate] - Conta de destino inativa: {}", destination.getId());
            throw new InactiveAccountException("Conta de destino inativa: " + destination.getId());
        }

        log.info("[AccountActiveValidationStrategy].[validate] - Contas ativas - Conta Origem: {} - Conta Destino: {}",
                source.getId(), destination.getId());
    }
}
