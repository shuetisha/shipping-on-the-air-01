package com.shippingontheair.delivery.domain.model;

import com.shippingontheair.delivery.domain.service.EtaCalculator;
import com.shippingontheair.delivery.domain.service.EtaCalculator.GeoCoordinates;
import java.time.Instant;
import java.util.UUID;

public class Delivery {

    private final UUID id;
    private final UUID shipmentId;
    private final UUID droneId;
    private DeliveryStatus status;
    private final GeoPoint origin;
    private final GeoPoint destination;
    private GeoPoint currentPosition;
    private double progressPercent;
    private int etaSeconds;
    private final double totalDistanceKm;
    private String failureReason;
    private final Instant createdAt;
    private Instant updatedAt;

    private Delivery(
            UUID id,
            UUID shipmentId,
            UUID droneId,
            DeliveryStatus status,
            GeoPoint origin,
            GeoPoint destination,
            GeoPoint currentPosition,
            double progressPercent,
            int etaSeconds,
            double totalDistanceKm,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.shipmentId = shipmentId;
        this.droneId = droneId;
        this.status = status;
        this.origin = origin;
        this.destination = destination;
        this.currentPosition = currentPosition;
        this.progressPercent = progressPercent;
        this.etaSeconds = etaSeconds;
        this.totalDistanceKm = totalDistanceKm;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Delivery start(
            UUID shipmentId,
            UUID droneId,
            GeoPoint origin,
            GeoPoint destination,
            double speedKmPerHour) {
        double distance = EtaCalculator.haversineKm(
                new GeoCoordinates(origin.latitude(), origin.longitude()),
                new GeoCoordinates(destination.latitude(), destination.longitude()));
        int eta = EtaCalculator.etaSecondsRemaining(distance, 0, speedKmPerHour);
        Instant now = Instant.now();
        return new Delivery(
                UUID.randomUUID(),
                shipmentId,
                droneId,
                DeliveryStatus.IN_TRANSIT,
                origin,
                destination,
                origin,
                0,
                eta,
                distance,
                null,
                now,
                now);
    }

    public void advanceProgress(double incrementPercent, double speedKmPerHour) {
        if (status != DeliveryStatus.IN_TRANSIT) {
            return;
        }
        progressPercent = Math.min(100, progressPercent + incrementPercent);
        currentPosition = interpolate(origin, destination, progressPercent / 100.0);
        etaSeconds = EtaCalculator.etaSecondsRemaining(totalDistanceKm, progressPercent, speedKmPerHour);
        if (progressPercent >= 100) {
            status = DeliveryStatus.DELIVERED;
            currentPosition = destination;
            etaSeconds = 0;
        }
        touch();
    }

    public void markFailed(String reason) {
        status = DeliveryStatus.FAILED;
        failureReason = reason;
        touch();
    }

    private static GeoPoint interpolate(GeoPoint from, GeoPoint to, double ratio) {
        return new GeoPoint(
                from.latitude() + (to.latitude() - from.latitude()) * ratio,
                from.longitude() + (to.longitude() - from.longitude()) * ratio);
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    public static Delivery restore(
            UUID id,
            UUID shipmentId,
            UUID droneId,
            DeliveryStatus status,
            GeoPoint origin,
            GeoPoint destination,
            GeoPoint currentPosition,
            double progressPercent,
            int etaSeconds,
            double totalDistanceKm,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        return new Delivery(
                id, shipmentId, droneId, status, origin, destination, currentPosition,
                progressPercent, etaSeconds, totalDistanceKm, failureReason, createdAt, updatedAt);
    }

    public UUID getId() { return id; }
    public UUID getShipmentId() { return shipmentId; }
    public UUID getDroneId() { return droneId; }
    public DeliveryStatus getStatus() { return status; }
    public GeoPoint getOrigin() { return origin; }
    public GeoPoint getDestination() { return destination; }
    public GeoPoint getCurrentPosition() { return currentPosition; }
    public double getProgressPercent() { return progressPercent; }
    public int getEtaSeconds() { return etaSeconds; }
    public double getTotalDistanceKm() { return totalDistanceKm; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
