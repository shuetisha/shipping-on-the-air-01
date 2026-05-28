CREATE TABLE deliveries (
    id UUID PRIMARY KEY,
    shipment_id UUID NOT NULL,
    drone_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    origin_latitude DOUBLE PRECISION NOT NULL,
    origin_longitude DOUBLE PRECISION NOT NULL,
    destination_latitude DOUBLE PRECISION NOT NULL,
    destination_longitude DOUBLE PRECISION NOT NULL,
    current_latitude DOUBLE PRECISION NOT NULL,
    current_longitude DOUBLE PRECISION NOT NULL,
    progress_percent DOUBLE PRECISION NOT NULL,
    eta_seconds INT NOT NULL,
    total_distance_km DOUBLE PRECISION NOT NULL,
    failure_reason VARCHAR(512),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
