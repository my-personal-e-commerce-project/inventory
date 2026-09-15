CREATE TABLE IF NOT EXISTS aggregate_outbox (
    id SERIAL PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE aggregate_outbox REPLICA IDENTITY FULL;
