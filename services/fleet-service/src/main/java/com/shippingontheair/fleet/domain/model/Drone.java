package com.shippingontheair.fleet.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Drone {

    private final UUID id;
    private final String name;
    private final double maxPayloadKg;
    private DroneStatus status;
    private double latitude;
    private double longitude;
    private final Instant createdAt;
    private Instant updatedAt;

    private Drone(
            UUID id,
            String name,
            double maxPayloadKg,
            DroneStatus status,
            double latitude,
            double longitude,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.maxPayloadKg = maxPayloadKg;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Drone register(String name, double maxPayloadKg, double latitude, double longitude) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (maxPayloadKg <= 0) {
            throw new IllegalArgumentException("maxPayloadKg must be positive");
        }
        Instant now = Instant.now();
        return new Drone(UUID.randomUUID(), name, maxPayloadKg, DroneStatus.AVAILABLE, latitude, longitude, now, now);
    }

    public void reserve(double requiredPayloadKg) {
        if (status != DroneStatus.AVAILABLE) {
            throw new IllegalStateException("drone is not available");
        }
        if (maxPayloadKg < requiredPayloadKg) {
            throw new IllegalArgumentException("payload exceeds drone capacity");
        }
        status = DroneStatus.IN_MISSION;
        touch();
    }

    public void release() {
        if (status != DroneStatus.IN_MISSION) {
            throw new IllegalStateException("drone is not in mission");
        }
        status = DroneStatus.AVAILABLE;
        touch();
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    public static Drone restore(
            UUID id,
            String name,
            double maxPayloadKg,
            DroneStatus status,
            double latitude,
            double longitude,
            Instant createdAt,
            Instant updatedAt) {
        return new Drone(id, name, maxPayloadKg, status, latitude, longitude, createdAt, updatedAt);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public double getMaxPayloadKg() { return maxPayloadKg; }
    public DroneStatus getStatus() { return status; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
