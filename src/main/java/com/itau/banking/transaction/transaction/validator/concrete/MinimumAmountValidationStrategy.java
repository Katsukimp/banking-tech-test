package com.itau.banking.transaction.transaction.validator.concrete;

import com.itau.banking.transaction.account.Account;
import com.itau.banking.transaction.shared.config.BankingProperties;
import com.itau.banking.transaction.shared.exception.MinimumAmountException;
import com.itau.banking.transaction.transaction.validator.ValidationOrder;
import com.itau.banking.transaction.transaction.validator.ValidationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
@ValidationOrder(3)
public class MinimumAmountValidationStrategy implements ValidationStrategy {

    private final BankingProperties bankingProperties;

    @Override
    public void validate(Account source, Account destination, BigDecimal amount) {
        log.info("[MinimumAmountValidator].[doValidate] - Validando valor mínimo da transferência - Valor da Transferência: {}", amount);

        if(amount.compareTo(bankingProperties.getTransfer().getMinimumAmount()) < 0){
            log.info("[MinimumAmountValidator].[doValidate] - Valor da transferência deve ser maior ou igual R${} - Valor da Transferência: {}"
                    ,bankingProperties.getTransfer().getMinimumAmount()
                    ,amount);

            throw new MinimumAmountException("O valor da transferência deve ser maior ou igual R$" + bankingProperties.getTransfer().getMinimumAmount());
        }

        log.info("[MinimumAmountValidator].[doValidate] - Valor mínimo da transferência validado com sucesso");
    }
}
