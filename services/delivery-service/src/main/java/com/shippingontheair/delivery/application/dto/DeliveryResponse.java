package com.shippingontheair.delivery.application.dto;

import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import java.time.Instant;
import java.util.UUID;

public record DeliveryResponse(
        UUID id,
        UUID shipmentId,
        UUID droneId,
        DeliveryStatus status,
        double progressPercent,
        int etaSeconds,
        String failureReason,
        Instant createdAt,
        Instant updatedAt) {

    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getShipmentId(),
                delivery.getDroneId(),
                delivery.getStatus(),
                delivery.getProgressPercent(),
                delivery.getEtaSeconds(),
                delivery.getFailureReason(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt());
    }
}
