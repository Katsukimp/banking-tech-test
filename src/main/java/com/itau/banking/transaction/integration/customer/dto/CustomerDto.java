package com.itau.banking.transaction.integration.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private String phone;
}
