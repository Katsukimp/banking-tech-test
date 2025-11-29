CREATE TABLE daily_limit_control (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    date DATE NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    transaction_count INTEGER NOT NULL DEFAULT 0,
    last_updated_at TIMESTAMP,
    CONSTRAINT uk_account_date UNIQUE (account_id, date)
);

CREATE INDEX idx_limit_account_date ON daily_limit_control(account_id, date);
CREATE INDEX idx_limit_date ON daily_limit_control(date);
