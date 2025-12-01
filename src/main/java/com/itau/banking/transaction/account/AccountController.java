package com.itau.banking.transaction.account;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@Tag(name = "Account", description = "Operações de consulta de conta corrente")
public class AccountController {

    private final AccountService accountService;

    @Tag(name = "Account", description = "Consulta informações de conta de corrente por ID")
    @GetMapping
    public ResponseEntity<Account> getAccountById(@RequestParam Long accountId) {
        return ResponseEntity.ok(accountService.findById(accountId));
    }

    @Tag(name = "Account", description = "Consulta de todas as contas correntes - (Cenário de teste)")
    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }

}
