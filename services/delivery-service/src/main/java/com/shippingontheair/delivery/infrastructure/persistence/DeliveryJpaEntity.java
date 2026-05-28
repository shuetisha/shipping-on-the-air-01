package com.shippingontheair.delivery.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
public class DeliveryJpaEntity {

    @Id
    private UUID id;
    @Column(name = "shipment_id", nullable = false)
    private UUID shipmentId;
    @Column(name = "drone_id", nullable = false)
    private UUID droneId;
    @Column(nullable = false)
    private String status;
    @Column(name = "origin_latitude", nullable = false)
    private double originLatitude;
    @Column(name = "origin_longitude", nullable = false)
    private double originLongitude;
    @Column(name = "destination_latitude", nullable = false)
    private double destinationLatitude;
    @Column(name = "destination_longitude", nullable = false)
    private double destinationLongitude;
    @Column(name = "current_latitude", nullable = false)
    private double currentLatitude;
    @Column(name = "current_longitude", nullable = false)
    private double currentLongitude;
    @Column(name = "progress_percent", nullable = false)
    private double progressPercent;
    @Column(name = "eta_seconds", nullable = false)
    private int etaSeconds;
    @Column(name = "total_distance_km", nullable = false)
    private double totalDistanceKm;
    @Column(name = "failure_reason")
    private String failureReason;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected DeliveryJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getShipmentId() { return shipmentId; }
    public void setShipmentId(UUID shipmentId) { this.shipmentId = shipmentId; }
    public UUID getDroneId() { return droneId; }
    public void setDroneId(UUID droneId) { this.droneId = droneId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getOriginLatitude() { return originLatitude; }
    public void setOriginLatitude(double originLatitude) { this.originLatitude = originLatitude; }
    public double getOriginLongitude() { return originLongitude; }
    public void setOriginLongitude(double originLongitude) { this.originLongitude = originLongitude; }
    public double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(double destinationLatitude) { this.destinationLatitude = destinationLatitude; }
    public double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(double destinationLongitude) { this.destinationLongitude = destinationLongitude; }
    public double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(double currentLatitude) { this.currentLatitude = currentLatitude; }
    public double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(double currentLongitude) { this.currentLongitude = currentLongitude; }
    public double getProgressPercent() { return progressPercent; }
    public void setProgressPercent(double progressPercent) { this.progressPercent = progressPercent; }
    public int getEtaSeconds() { return etaSeconds; }
    public void setEtaSeconds(int etaSeconds) { this.etaSeconds = etaSeconds; }
    public double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
