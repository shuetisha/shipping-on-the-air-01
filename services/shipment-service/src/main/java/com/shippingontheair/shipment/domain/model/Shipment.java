package com.shippingontheair.shipment.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Shipment {

    private final UUID id;
    private final GeoPoint origin;
    private final GeoPoint destination;
    private final double weightKg;
    private final ScheduleType scheduleType;
    private final Instant scheduledAt;
    private ShipmentStatus status;
    private String failureReason;
    private final Instant createdAt;
    private Instant updatedAt;

    private Shipment(
            UUID id,
            GeoPoint origin,
            GeoPoint destination,
            double weightKg,
            ScheduleType scheduleType,
            Instant scheduledAt,
            ShipmentStatus status,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.weightKg = weightKg;
        this.scheduleType = scheduleType;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Shipment create(
            GeoPoint origin,
            GeoPoint destination,
            double weightKg,
            ScheduleType scheduleType,
            Instant scheduledAt) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException("weight must be positive");
        }
        if (scheduleType == ScheduleType.SCHEDULED) {
            if (scheduledAt == null) {
                throw new IllegalArgumentException("scheduledAt required for SCHEDULED");
            }
            if (scheduledAt.isBefore(Instant.now())) {
                throw new IllegalArgumentException("scheduledAt cannot be in the past");
            }
        }
        Instant now = Instant.now();
        return new Shipment(
                UUID.randomUUID(),
                origin,
                destination,
                weightKg,
                scheduleType,
                scheduledAt,
                ShipmentStatus.REQUESTED,
                null,
                now,
                now);
    }

    public void markDispatched() {
        if (status != ShipmentStatus.REQUESTED) {
            throw new IllegalStateException("only REQUESTED shipments can be dispatched");
        }
        status = ShipmentStatus.DISPATCHED;
        touch();
    }

    public void markDelivered() {
        if (status != ShipmentStatus.DISPATCHED) {
            throw new IllegalStateException("only DISPATCHED shipments can be delivered");
        }
        status = ShipmentStatus.DELIVERED;
        touch();
    }

    public void markFailed(String reason) {
        if (status == ShipmentStatus.DELIVERED || status == ShipmentStatus.CANCELLED) {
            throw new IllegalStateException("cannot fail terminal shipment");
        }
        status = ShipmentStatus.FAILED;
        failureReason = reason;
        touch();
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    public static Shipment restore(
            UUID id,
            GeoPoint origin,
            GeoPoint destination,
            double weightKg,
            ScheduleType scheduleType,
            Instant scheduledAt,
            ShipmentStatus status,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        return new Shipment(
                id, origin, destination, weightKg, scheduleType, scheduledAt,
                status, failureReason, createdAt, updatedAt);
    }

    public UUID getId() { return id; }
    public GeoPoint getOrigin() { return origin; }
    public GeoPoint getDestination() { return destination; }
    public double getWeightKg() { return weightKg; }
    public ScheduleType getScheduleType() { return scheduleType; }
    public Instant getScheduledAt() { return scheduledAt; }
    public ShipmentStatus getStatus() { return status; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
