-- Inserir contas de teste (customerIds 1-5 existem no MOCK_CUSTOMERS)
INSERT INTO accounts (account_number, balance, daily_limit, status, customer_id, created_at) VALUES
('12345-6', 5000.00, 1000.00, 'ACTIVE', 1, CURRENT_TIMESTAMP),
('78901-2', 3000.00, 1000.00, 'ACTIVE', 2, CURRENT_TIMESTAMP),
('34567-8', 10000.00, 1000.00, 'ACTIVE', 3, CURRENT_TIMESTAMP),
('90123-4', 500.00, 1000.00, 'ACTIVE', 4, CURRENT_TIMESTAMP),
('56789-0', 0.00, 1000.00, 'INACTIVE', 5, CURRENT_TIMESTAMP);
