CREATE TABLE bacen_notifications (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    idempotency_key VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payload TEXT,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_attempt_at TIMESTAMP,
    sent_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE INDEX idx_bacen_status_created ON bacen_notifications(status, created_at);
CREATE INDEX idx_bacen_idempotency ON bacen_notifications(idempotency_key);
CREATE INDEX idx_bacen_transaction ON bacen_notifications(transaction_id);
CREATE INDEX idx_bacen_status ON bacen_notifications(status);
