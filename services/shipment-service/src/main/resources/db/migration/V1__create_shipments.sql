CREATE TABLE shipments (
    id UUID PRIMARY KEY,
    origin_label VARCHAR(255) NOT NULL,
    origin_latitude DOUBLE PRECISION NOT NULL,
    origin_longitude DOUBLE PRECISION NOT NULL,
    destination_label VARCHAR(255) NOT NULL,
    destination_latitude DOUBLE PRECISION NOT NULL,
    destination_longitude DOUBLE PRECISION NOT NULL,
    weight_kg DOUBLE PRECISION NOT NULL,
    schedule_type VARCHAR(32) NOT NULL,
    scheduled_at TIMESTAMP,
    status VARCHAR(32) NOT NULL,
    failure_reason VARCHAR(512),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
