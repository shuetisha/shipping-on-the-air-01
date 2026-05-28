package com.shippingontheair.shipment.application.dto;

import com.shippingontheair.shipment.domain.model.ScheduleType;
import com.shippingontheair.shipment.domain.model.Shipment;
import com.shippingontheair.shipment.domain.model.ShipmentStatus;
import java.time.Instant;
import java.util.UUID;

public record ShipmentResponse(
        UUID id,
        LocationDto origin,
        LocationDto destination,
        double weightKg,
        ScheduleType scheduleType,
        Instant scheduledAt,
        ShipmentStatus status,
        String failureReason,
        Instant createdAt,
        Instant updatedAt) {

    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                new LocationDto(
                        shipment.getOrigin().label(),
                        shipment.getOrigin().latitude(),
                        shipment.getOrigin().longitude()),
                new LocationDto(
                        shipment.getDestination().label(),
                        shipment.getDestination().latitude(),
                        shipment.getDestination().longitude()),
                shipment.getWeightKg(),
                shipment.getScheduleType(),
                shipment.getScheduledAt(),
                shipment.getStatus(),
                shipment.getFailureReason(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt());
    }
}
