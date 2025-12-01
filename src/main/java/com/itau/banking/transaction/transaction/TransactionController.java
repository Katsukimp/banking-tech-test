package com.itau.banking.transaction.transaction;

import com.itau.banking.transaction.shared.exception.DuplicateTransactionException;
import com.itau.banking.transaction.shared.idempotency.IdempotencyService;
import com.itau.banking.transaction.transaction.dto.TransferRequest;
import com.itau.banking.transaction.transaction.dto.TransferResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
@Tag(name = "Transaction", description = "Operações de transações bancárias")
public class TransactionController {

    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey,
            @RequestBody TransferRequest request
    ) {
        if (idempotencyKey != null && !idempotencyService.isValidIdempotencyKey(idempotencyKey)) {
            Long existingTransactionId = idempotencyService.getTransactionByIdempotencyKey(idempotencyKey);
            throw new DuplicateTransactionException("Transaction already processed with idempotency key: " + idempotencyKey + ". Transaction ID: " + existingTransactionId);
        }
        
        TransferResponse response = transactionService.transfer(request, idempotencyKey);
        return ResponseEntity.ok(response);
    }
}
