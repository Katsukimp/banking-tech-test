package com.itau.banking.transaction.account;

import com.itau.banking.transaction.shared.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    public Account findById(Long accountId) {
        log.info("[AccountService].[findById] - Buscando conta: {}", accountId);
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public void debit(Account account, BigDecimal amount) {
        log.info("[AccountService].[debit] - Debitando {} da conta {}", amount, account.getId());
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    public void credit(Account account, BigDecimal amount) {
        log.info("[AccountService].[credit] - Creditando {} na conta {}", amount, account.getId());
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    public List<Account> findAll(){
        log.info("[AccountService].[findAll] - Buscando todas as contas");
        return accountRepository.findAll();
    }
}
