package com.itau.banking.transaction.integration.customer;

import com.itau.banking.transaction.shared.exception.CustomerNotFoundException;
import com.itau.banking.transaction.integration.customer.dto.CustomerDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CustomerApiClient {
    
    private static final Map<Long, CustomerDto> MOCK_CUSTOMERS = new ConcurrentHashMap<>();
    
    static {
        MOCK_CUSTOMERS.put(1L, CustomerDto.builder()
                .id(1L)
                .name("João da Silva")
                .cpf("123.456.789-00")
                .email("joao.silva@email.com")
                .phone("(11) 98765-4321")
                .build());
        
        MOCK_CUSTOMERS.put(2L, CustomerDto.builder()
                .id(2L)
                .name("Maria Santos")
                .cpf("987.654.321-00")
                .email("maria.santos@email.com")
                .phone("(11) 91234-5678")
                .build());
        
        MOCK_CUSTOMERS.put(3L, CustomerDto.builder()
                .id(3L)
                .name("Pedro Oliveira")
                .cpf("456.789.123-00")
                .email("pedro.oliveira@email.com")
                .phone("(11) 99876-5432")
                .build());
        
        MOCK_CUSTOMERS.put(4L, CustomerDto.builder()
                .id(4L)
                .name("Ana Costa")
                .cpf("321.654.987-00")
                .email("ana.costa@email.com")
                .phone("(11) 94567-8901")
                .build());
        
        MOCK_CUSTOMERS.put(5L, CustomerDto.builder()
                .id(5L)
                .name("Carlos Ferreira")
                .cpf("789.123.456-00")
                .email("carlos.ferreira@email.com")
                .phone("(11) 93456-7890")
                .build());
    }

    @CircuitBreaker(name = "customerApi", fallbackMethod = "findCustomerByIdFallback")
    @Retry(name = "customerApi")
    public CustomerDto findCustomerById(Long customerId) {
        log.info("Mock CustomerAPI: Consultando cliente ID {}", customerId);
        
        simulateNetworkLatency();
        CustomerDto customer = MOCK_CUSTOMERS.get(customerId);
        
        if (customer == null) {
            log.warn("Mock CustomerAPI: Cliente {} não encontrado", customerId);
            throw new CustomerNotFoundException("Cliente não encontrado: " + customerId);
        }
        
        log.info("Mock CustomerAPI: Cliente {} encontrado - {}", customerId, customer.getName());
        return customer;
    }

    private CustomerDto findCustomerByIdFallback(Long customerId, Exception ex) {
        log.error("[CustomerApiClient].[fallback] - Erro ao buscar customer {}: {}", customerId, ex.getMessage());
        throw new CustomerNotFoundException(customerId);
    }

    private void simulateNetworkLatency() {
        try {
            long latency = 10 + (long) (Math.random() * 40);
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
