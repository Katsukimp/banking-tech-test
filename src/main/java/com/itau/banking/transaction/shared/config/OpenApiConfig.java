package com.itau.banking.transaction.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banking Transaction API - Itaú")
                        .version("1.0.0")
                        .description("API de transações bancárias com notificação BACEN, controle de limite diário e idempotência")
                        .contact(new Contact()
                                .name("Itaú Banking Team")
                                .email("banking-api@itau.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Ambiente Local"),
                        new Server()
                                .url("https://api.itau.com.br")
                                .description("Ambiente Produção")
                ));
    }
}
