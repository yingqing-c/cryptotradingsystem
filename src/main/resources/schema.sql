-- schema.sql for H2 database

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Wallets table
CREATE TABLE wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    balance DECIMAL(20,8) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT unique_user_currency UNIQUE (user_id, currency)
);

-- Trades table
CREATE TABLE trades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    trade_type VARCHAR(4) NOT NULL,
    quantity DECIMAL(20,8) NOT NULL,
    price DECIMAL(20,8) NOT NULL,
    total_trade_value DECIMAL(20,8) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Prices table
CREATE TABLE prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    bid_price DECIMAL(20,8) NOT NULL,
    bid_source VARCHAR(20),
    ask_price DECIMAL(20,8) NOT NULL,
    ask_source VARCHAR(20),
    timestamp TIMESTAMP NOT NULL
);
CREATE INDEX idx_symbol_timestamp ON prices(symbol, timestamp DESC);