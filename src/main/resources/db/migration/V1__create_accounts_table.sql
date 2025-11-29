CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    daily_limit DECIMAL(15, 2) NOT NULL DEFAULT 1000.00,
    status VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_customer_id ON accounts(customer_id);
CREATE INDEX idx_account_status ON accounts(status);
