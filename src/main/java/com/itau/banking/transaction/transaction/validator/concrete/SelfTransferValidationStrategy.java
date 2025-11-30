package com.itau.banking.transaction.transaction.validator.concrete;

import com.itau.banking.transaction.account.Account;
import com.itau.banking.transaction.shared.exception.SelfTransferException;
import com.itau.banking.transaction.transaction.validator.ValidationOrder;
import com.itau.banking.transaction.transaction.validator.ValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@ValidationOrder(2)
public class SelfTransferValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(Account source, Account destination, BigDecimal amount) {
        log.info("[SelfTransferValidator].[doValidate] - Validando transferência para a mesma conta - Conta de Origem: {} - Conta de Destino: {}",
                source.getId(), destination.getId());

        if(source.getId().equals(destination.getId())){
            log.error("[SelfTransferValidator].[doValidate] - Transferência para a mesma conta não é permitida - Conta: {}",
                    source.getId());
            throw new SelfTransferException();
        }

        log.info("[SelfTransferValidator].[doValidate] - Transferência para a mesma conta validada com sucesso");
    }
}
