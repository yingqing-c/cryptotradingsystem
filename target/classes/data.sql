INSERT INTO users (username, created_at, updated_at)
VALUES
    ('testuser', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wallets (user_id, currency, balance)
SELECT 1, 'USDT', 50000.00000000
WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id = 1 AND currency = 'USDT');

INSERT INTO wallets (user_id, currency, balance)
SELECT 1, 'BTCUSDT', 0.00000000
WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id = 1 AND currency = 'BTCUSDT');

INSERT INTO wallets (user_id, currency, balance)
SELECT 1, 'ETHUSDT', 0.00000000
WHERE NOT EXISTS (SELECT 1 FROM wallets WHERE user_id = 1 AND currency = 'ETHUSDT');