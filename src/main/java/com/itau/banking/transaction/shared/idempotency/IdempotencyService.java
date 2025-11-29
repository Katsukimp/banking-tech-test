package com.itau.banking.transaction.shared.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);
    
    public boolean isValidIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            log.warn("Idempotency key is null or blank");
            return false;
        }
        
        String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
        Boolean exists = redisTemplate.hasKey(key);
        
        if (Boolean.TRUE.equals(exists)) {
            log.warn("Duplicate idempotency key detected: {}", idempotencyKey);
            return false;
        }
        
        return true;
    }
    
    public void registerIdempotencyKey(String idempotencyKey, Long transactionId) {
        String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
        redisTemplate.opsForValue().set(key, transactionId.toString(), IDEMPOTENCY_TTL);
        log.info("Idempotency key registered: {} -> Transaction: {}", idempotencyKey, transactionId);
    }
    
    public Long getTransactionByIdempotencyKey(String idempotencyKey) {
        String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
        String transactionId = redisTemplate.opsForValue().get(key);
        
        if (transactionId != null) {
            return Long.parseLong(transactionId);
        }
        
        return null;
    }
    
    public String generateIdempotencyKey() {
        return UUID.randomUUID().toString();
    }
}
