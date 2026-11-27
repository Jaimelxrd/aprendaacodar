CREATE TABLE platform_settings (
    id UUID PRIMARY KEY,
    mpesa_number VARCHAR(50),
    emola_number VARCHAR(50),
    account_holder_name VARCHAR(180),
    payment_instructions TEXT,
    institutional_information TEXT,
    updated_by_id UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

INSERT INTO platform_settings (id, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
