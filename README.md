# 🏦 Banking Transaction API

> **Microserviço de Transações Bancárias com Notificação BACEN**  
> Sistema de alto desempenho para processamento de transferências entre contas com garantia de idempotência, resiliência e observabilidade.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)
[![Kafka](https://img.shields.io/badge/Kafka-3.9.1-black.svg)](https://kafka.apache.org/)

---

## 📋 Sumário

- [Visão Geral](#-visão-geral)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Stack Tecnológica](#-stack-tecnológica)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Execução](#-execução)
- [API Endpoints](#-api-endpoints)
- [Testes](#-testes)
- [Configuração](#-configuração)
- [Monitoramento](#-monitoramento)

---

## 🎯 Visão Geral

A **Banking Transaction API** é um microserviço RESTful desenvolvido para fintechs que necessitam de um sistema robusto de transferências bancárias com notificação obrigatória ao Banco Central (BACEN). O serviço foi projetado com foco em:

- ⚡ **Alta Performance**: Suporta ~150 requisições/segundo com latência P99 < 100ms
- 🔒 **Segurança**: Idempotência garantida via Redis para prevenir duplicações
- 🔄 **Resiliência**: Circuit Breaker, Retry e fallback assíncrono via Kafka
- 📊 **Observabilidade**: Métricas Prometheus, health checks e distributed tracing
- ✅ **Qualidade**: 100% de cobertura de testes (52 testes automatizados)

### Casos de Uso

✅ Transferências P2P (pessoa para pessoa)  
✅ Validação de saldo e limites em tempo real  
✅ Notificação ao BACEN com garantia de entrega  
✅ Prevenção de transações duplicadas  
✅ Controle de limite diário por conta  

---

## 🚀 Funcionalidades

### Core Business

#### 1. Transferência entre Contas
- Débito da conta de origem
- Crédito na conta de destino
- Transação ACID garantida
- Idempotência por `Idempotency-Key`

#### 2. Validações Automáticas
Sistema de validação em cadeia (Chain of Responsibility):

| Validação | Descrição |
|-----------|-----------|
| **Conta Ativa** | Verifica se origem e destino estão ativos |
| **Self Transfer** | Bloqueia transferência para mesma conta |
| **Valor Mínimo** | Mínimo de R$ 0,01 por transação |
| **Saldo Suficiente** | Valida saldo disponível na origem |
| **Limite Diário** | Controla limite configurável por conta |

#### 3. Notificação BACEN
- **Modo Síncrono**: Tentativa imediata ao processar transação
- **Fallback Assíncrono**: Envia para Kafka em caso de falha
- **Retry Automático**: Consumidor Kafka com 3 tentativas
- **Protocolo**: Retorna número de protocolo BACEN

#### 4. Integração com Customer API
- Cache Redis com TTL de 24h
- Circuit Breaker para proteção
- Retry em caso de timeout
- Fallback para dados mockados

---

## 🏗️ Arquitetura

### Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                     Cliente (Frontend/Mobile)               │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP/REST
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                 Banking Transaction API                     │
│  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐        │
│  │ Controller   │→ │  Service    │→ │  Repository  │        │
│  └──────────────┘  └─────────────┘  └──────────────┘        │
│         │                 │                   │             │
│         │          ┌──────┴──────┐            │             │
│         │          │ Validations │            │             │
│         │          │  (Strategy) │            │             │
│         │          └─────────────┘            │             │
└─────────┼──────────────────┬──────────────────┼─────────────┘
          │                  │                  │
    ┌─────▼─────┐     ┌──────▼────────┐  ┌────▼──────┐
    │   Redis   │     │  Kafka Topic  │  │PostgreSQL │
    │   Cache/  │     │ (bacen-notify)│  │  (ACID)   │
    │Idempotency│     └───────┬───────┘  └───────────┘
    └───────────┘             │
                              │
                    ┌─────────▼──────────┐
                    │ Kafka Consumer     │
                    │  (5 threads)       │
                    └─────────┬──────────┘
                              │
                    ┌─────────▼──────────┐
                    │   BACEN API        │
                    │ (Circuit Breaker)  │
                    └────────────────────┘
```

### Fluxo de Transação Completo

```
1. Cliente envia POST /transfer com Idempotency-Key
   │
2. ┌─ Verificação de Idempotência (Redis)
   │  ├─ Se duplicada → Retorna transação existente (409)
   │  └─ Se nova → Continua
   │
3. ┌─ Busca Contas (PostgreSQL com Pessimistic Lock)
   │
4. ┌─ Busca Dados do Cliente (Customer API + Cache Redis)
   │
5. ┌─ Executa Validações (Strategy Pattern)
   │  ├─ Conta Ativa
   │  ├─ Self Transfer
   │  ├─ Valor Mínimo
   │  ├─ Saldo Suficiente
   │  └─ Limite Diário
   │
6. ┌─ Executa Transação (ACID)
   │  ├─ Debita origem
   │  ├─ Credita destino
   │  ├─ Salva registro de transação
   │  └─ Registra idempotência
   │
7. ┌─ Notificação BACEN
   │  ├─ Tenta envio síncrono
   │  │  ├─ Sucesso → Retorna protocolo (200 OK)
   │  │  └─ Falha → Envia para Kafka
   │  └─ Kafka Consumer processa em background
   │
8. ┌─ Resposta ao Cliente
   └─ TransferResponse com dados da transação
```

---

## 🛠️ Stack Tecnológica

### Backend Core
- **Java 21** - Linguagem base com recursos modernos (Virtual Threads ready)
- **Spring Boot 3.5.8** - Framework principal
- **Maven** - Gerenciamento de dependências

### Persistência
- **PostgreSQL 15** - Banco relacional ACID
- **Flyway** - Versionamento de schema
- **Spring Data JPA** - ORM e abstração de dados
- **HikariCP** - Connection pool otimizado (50 conexões)

### Cache & Mensageria
- **Redis** - Cache distribuído + Idempotência (pool de 50 conexões)
- **Apache Kafka 3.9.1** - Mensageria assíncrona (5 threads consumidoras)
- **Zookeeper** - Coordenação do cluster Kafka

### Resiliência
- **Resilience4j** - Circuit Breaker, Retry, Rate Limiter
  - Circuit Breaker: 50% failure threshold
  - Retry: 2 tentativas para Customer API, 1 para BACEN
  - Rate Limiter: 100 req/s para BACEN

### Observabilidade
- **Spring Actuator** - Health checks e métricas
- **Micrometer** - Coleta de métricas
- **Prometheus** - Exportação de métricas
- **Swagger/OpenAPI** - Documentação interativa da API

### Testes
- **JUnit 5** - Framework de testes unitários
- **Mockito** - Mocking
- **Testcontainers** - Testes de integração com containers
- **Gatling 4.10.1** - Testes de carga e performance
- **JaCoCo** - Cobertura de código

### DevOps
- **Docker** - Containerização
- **Docker Compose** - Orquestração local

---

## 📦 Pré-requisitos

### Obrigatórios

| Software | Versão Mínima | Verificação |
|----------|---------------|-------------|
| Java JDK | 21 | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Docker | 20+ | `docker --version` |
| Docker Compose | 2.0+ | `docker-compose --version` |

### Recomendado

- **8GB RAM** disponível para containers
- **10GB** de espaço em disco
- **Git** para clonar o repositório

### Verificação do Ambiente

```bash
# Verificar todas as dependências
java -version      # Deve mostrar "21" ou superior
mvn -version       # Deve mostrar "3.8" ou superior
docker --version   # Deve estar instalado
docker-compose --version
```

---

## 📥 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/Katsukimp/itau-tech-test.git
cd itau-tech-test
```

### 2. Subir Infraestrutura (Docker Compose)

⚠️ **IMPORTANTE**: A aplicação **REQUER** que os containers Docker estejam rodando antes de iniciar.

```bash
# Iniciar todos os serviços em background
docker-compose up -d

# Verificar se os containers estão rodando
docker-compose ps

# Aguardar inicialização (PostgreSQL demora ~10s)
docker-compose logs -f postgres
```

**Serviços Disponíveis:**

| Serviço | Porta | Credenciais | Uso |
|---------|-------|-------------|-----|
| PostgreSQL | 5432 | admin/admin | Banco de dados principal |
| Redis | 6379 | - | Cache + Idempotência |
| Kafka | 9092 | - | Mensageria assíncrona |
| Zookeeper | 2181 | - | Coordenação Kafka |
| PgAdmin | 5050 | admin@admin.com/admin | Interface web do PostgreSQL |

### 3. Compilar o Projeto

```bash
# Baixar dependências e compilar (sem executar testes)
./mvnw clean install -DskipTests

# Ou apenas compilar
./mvnw clean compile
```

---

## 🚀 Execução

### Modo Desenvolvimento

```bash
# Opção 1: Usando Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Opção 2: Executar JAR compilado
./mvnw clean package -DskipTests
java -jar target/banking-transaction-api-0.0.1-SNAPSHOT.jar
```

### Verificar se a Aplicação Subiu

```bash
# Health check
curl http://localhost:8080/actuator/health

# Deve retornar:
# {"status":"UP"}
```

**URLs Importantes:**

- **API Base:** http://localhost:8080/api/v1
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health
- **Métricas:** http://localhost:8080/actuator/prometheus
- **PgAdmin:** http://localhost:5050

### Logs da Aplicação

```bash
# Ver logs em tempo real
docker-compose logs -f

# Logs apenas da aplicação (quando rodando em container)
docker-compose logs -f banking-transaction-api

# Logs de um serviço específico
docker-compose logs -f postgres
docker-compose logs -f redis
docker-compose logs -f kafka
```

### Parar a Aplicação

```bash
# Parar containers (mantém dados)
docker-compose stop

# Parar e remover containers + volumes (LIMPA TUDO)
docker-compose down -v

# Apenas parar a aplicação Spring Boot
Ctrl + C
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/v1
```

### 1. Realizar Transferência

**Endpoint:** `POST /transaction/transfer`

**Headers:**
```http
Content-Type: application/json
Idempotency-Key: <UUID único>
```

**Request Body:**
```json
{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 100.00
}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "transactionId": 123,
  "status": "SUCCESS",
  "sourceAccount": {
    "accountId": 1,
    "accountNumber": "ACC-001",
    "balance": 4900.00
  },
  "destinationAccount": {
    "accountId": 2,
    "accountNumber": "ACC-002",
    "balance": 5100.00
  },
  "amount": 100.00,
  "timestamp": "2025-11-30T10:30:45"
}
```

**Códigos de Erro:**

| Código | Descrição | Causa |
|--------|-----------|-------|
| 400 | Bad Request | Saldo insuficiente, limite excedido, valor inválido |
| 404 | Not Found | Conta não encontrada |
| 409 | Conflict | Transação duplicada (idempotency key já usada) |
| 422 | Unprocessable Entity | Conta inativa, transferência para mesma conta |
| 500 | Internal Server Error | Erro inesperado no servidor |

**Exemplo de Erro (400):**
```json
{
  "timestamp": "2025-11-30T10:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo insuficiente. Saldo atual: R$ 50,00, Valor solicitado: R$ 100,00",
  "path": "/api/v1/transaction/transfer"
}
```

### 2. Listar Contas

**Endpoint:** `GET /transaction/get-accounts`

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "accountNumber": "ACC-001",
    "balance": 5000.00,
    "dailyLimit": 1000.00,
    "customerId": 1,
    "active": true
  },
  {
    "id": 2,
    "accountNumber": "ACC-002",
    "balance": 5000.00,
    "dailyLimit": 1000.00,
    "customerId": 2,
    "active": true
  }
]
```

### Exemplos de Uso

#### cURL

```bash
# 1. Realizar transferência
curl -X POST http://localhost:8080/api/v1/transaction/transfer \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $(uuidgen)" \
  -d '{
    "sourceAccountId": 1,
    "destinationAccountId": 2,
    "amount": 100.00
  }'

# 2. Listar contas
curl http://localhost:8080/api/v1/transaction/get-accounts
```

#### PowerShell

```powershell
# 1. Realizar transferência
$headers = @{
    "Content-Type" = "application/json"
    "Idempotency-Key" = [guid]::NewGuid().ToString()
}
$body = @{
    sourceAccountId = 1
    destinationAccountId = 2
    amount = 100.00
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/transaction/transfer" -Headers $headers -Body $body

# 2. Listar contas
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/transaction/get-accounts"
```

### Swagger UI

Acesse a documentação interativa em:
```
http://localhost:8080/swagger-ui.html
```

Você pode testar todos os endpoints diretamente pelo navegador.

---

## 🧪 Testes

### Executar Todos os Testes

```bash
# Rodar todos os testes (unit + integration + component)
./mvnw clean test

# Rodar testes e gerar relatório de cobertura
./mvnw clean test jacoco:report
```

**Resultado Esperado:**
```
Tests run: 52, Failures: 0, Errors: 0, Skipped: 0
```

### Tipos de Testes

| Tipo | Quantidade | Descrição |
|------|------------|-----------|
| **Unit Tests** | 38 | Testes isolados de classes e métodos |
| **Integration Tests** | 6 | Testes com banco de dados (Testcontainers) |
| **Component Tests** | 8 | Testes end-to-end da API |

### Cobertura de Código

```bash
# Gerar relatório JaCoCo
./mvnw clean test jacoco:report

# Abrir relatório no navegador
# Windows
start target/site/jacoco/index.html

# Linux/Mac
open target/site/jacoco/index.html
```

### Testes de Carga (Gatling)

⚠️ **Pré-requisito:** Aplicação deve estar rodando antes de executar os testes de carga.

```bash
# 1. Iniciar a aplicação em um terminal
./mvnw spring-boot:run

# 2. Em outro terminal, executar Gatling
./mvnw gatling:test
```

**Cenários de Teste:**

| Cenário | RPS | Duração | Total Requests |
|---------|-----|---------|----------------|
| Ramp-up | 5→50 | 30s | ~2,300 |
| Constant | 100 | 30s | ~3,000 |
| Stress | 50→150 | 10s | ~1,300 |
| **TOTAL** | - | ~77s | **~8,000** |

**Métricas Esperadas:**
- ✅ P99 < 100ms
- ✅ Taxa de sucesso > 95%
- ✅ Throughput: 80-150 RPS

**Visualizar Relatório:**
```bash
# O relatório é gerado em:
target/gatling/bankingtransactionloadtest-[timestamp]/index.html

# Abrir no navegador (Windows)
start target/gatling/bankingtransactionloadtest-*/index.html
```

### Executar Testes Específicos

```bash
# Apenas testes unitários de validação
./mvnw test -Dtest=*ValidationStrategyTest

# Apenas testes de integração
./mvnw test -Dtest=*IntegrationTest

# Apenas testes de um componente específico
./mvnw test -Dtest=IdempotencyServiceTest
```

---

## ⚙️ Configuração

### Arquivo de Configuração

Localizado em: `src/main/resources/application.properties`

### Configurações Principais

#### Banco de Dados
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/itau_banking
spring.datasource.username=admin
spring.datasource.password=admin
spring.datasource.hikari.maximum-pool-size=50
spring.datasource.hikari.minimum-idle=10
```

#### Redis
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.lettuce.pool.max-active=50
spring.data.redis.lettuce.pool.max-idle=20
```

#### Kafka
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=banking-transaction-api-group
spring.kafka.listener.concurrency=5
```

#### Servidor
```properties
server.port=8080
server.tomcat.threads.max=600
server.tomcat.threads.min-spare=50
```

#### Regras de Negócio
```properties
# Valor mínimo de transferência
banking.transfer.minimum-amount=0.01

# Cache TTL (horas)
banking.cache.customer.ttl-hours=24
banking.cache.idempotency.ttl-hours=24

# Retry de notificação
banking.notification.max-retry-attempts=3
banking.notification.max-failed-attempts=10
```

### Profiles

```bash
# Desenvolvimento (padrão)
./mvnw spring-boot:run

# Produção
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Testes
./mvnw test -Dspring.profiles.active=test
```

---

## 📊 Monitoramento

### Health Checks

```bash
# Status geral
curl http://localhost:8080/actuator/health

# Status detalhado
curl http://localhost:8080/actuator/health | jq
```

**Resposta:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "kafka": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

### Métricas Disponíveis

```bash
# Listar todas as métricas
curl http://localhost:8080/actuator/metrics

# Métrica específica
curl http://localhost:8080/actuator/metrics/http.server.requests
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Métricas Prometheus

```bash
# Exportar para Prometheus
curl http://localhost:8080/actuator/prometheus
```

### Principais Métricas

| Métrica | Descrição |
|---------|-----------|
| `http_server_requests_seconds` | Latência das requisições HTTP |
| `hikaricp_connections_active` | Conexões ativas do pool |
| `jvm_memory_used_bytes` | Memória JVM utilizada |
| `kafka_consumer_records_consumed_total` | Mensagens Kafka consumidas |
| `resilience4j_circuitbreaker_state` | Estado do Circuit Breaker |

---

## 🐳 Comandos Docker Úteis

```bash
# Ver status dos containers
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f

# Logs de um serviço específico
docker-compose logs -f postgres
docker-compose logs -f redis
docker-compose logs -f kafka

# Reiniciar um serviço
docker-compose restart postgres

# Parar todos os serviços
docker-compose stop

# Parar e remover (CUIDADO: apaga dados)
docker-compose down -v

# Reconstruir imagens
docker-compose up -d --build

# Acessar shell de um container
docker-compose exec postgres psql -U admin -d itau_banking
docker-compose exec redis redis-cli
```

---

## 🗄️ Banco de Dados

### Schema Principal

**Tabela: accounts**
```sql
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) UNIQUE NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    daily_limit DECIMAL(15,2) NOT NULL,
    customer_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER DEFAULT 0
);
```

**Tabela: transactions**
```sql
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    source_account_id BIGINT REFERENCES accounts(id),
    destination_account_id BIGINT REFERENCES accounts(id),
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Acessar Banco via PgAdmin

1. Abra: http://localhost:5050
2. Login: `admin@admin.com` / `admin`
3. Adicionar servidor:
   - Host: `postgres`
   - Port: `5432`
   - Database: `itau_banking`
   - Username: `admin`
   - Password: `admin`

### Consultas Úteis

```sql
-- Ver todas as contas
SELECT * FROM accounts;

-- Ver transações recentes
SELECT * FROM transactions ORDER BY created_at DESC LIMIT 10;

-- Verificar saldo de uma conta
SELECT account_number, balance FROM accounts WHERE id = 1;

-- Ver notificações BACEN pendentes
SELECT * FROM bacen_notifications WHERE status = 'PENDING';
```

---

## 🔧 Troubleshooting

### Problema: Aplicação não inicia

**Erro:** `Connection refused to localhost:5432`

**Solução:**
```bash
# Verificar se PostgreSQL está rodando
docker-compose ps

# Se não estiver, iniciar
docker-compose up -d postgres

# Aguardar até estar healthy
docker-compose logs -f postgres
```

### Problema: Testes falhando

**Erro:** `Testcontainers could not start`

**Solução:**
```bash
# Verificar se Docker está rodando
docker ps

# Verificar espaço em disco
docker system df

# Limpar containers antigos
docker system prune -a
```

### Problema: Kafka não conecta

**Solução:**
```bash
# Reiniciar Kafka e Zookeeper
docker-compose restart zookeeper kafka

# Aguardar inicialização (30 segundos)
docker-compose logs -f kafka
```

### Problema: Redis timeout

**Solução:**
```bash
# Verificar se Redis está respondendo
docker-compose exec redis redis-cli ping
# Deve retornar: PONG

# Se não, reiniciar
docker-compose restart redis
```

---

## 📝 Licença

Este projeto é licenciado sob a licença MIT - veja o arquivo LICENSE para detalhes.

---

## 👤 Autor

**Eduardo Katsuki**  
GitHub: [@Katsukimp](https://github.com/Katsukimp)  
Repository: [itau-tech-test](https://github.com/Katsukimp/itau-tech-test)

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

---

## 📞 Suporte

Para dúvidas ou problemas:
- 📧 Abra uma [issue no GitHub](https://github.com/Katsukimp/itau-tech-test/issues)
- 📖 Consulte a [documentação Swagger](http://localhost:8080/swagger-ui.html)
- 💬 Entre em contato com a equipe de desenvolvimento
