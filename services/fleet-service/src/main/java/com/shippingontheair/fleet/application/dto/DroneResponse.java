package com.shippingontheair.fleet.application.dto;

import com.shippingontheair.fleet.domain.model.Drone;
import com.shippingontheair.fleet.domain.model.DroneStatus;
import java.time.Instant;
import java.util.UUID;

public record DroneResponse(
        UUID id,
        String name,
        double maxPayloadKg,
        DroneStatus status,
        double latitude,
        double longitude,
        Instant createdAt,
        Instant updatedAt) {

    public static DroneResponse from(Drone drone) {
        return new DroneResponse(
                drone.getId(),
                drone.getName(),
                drone.getMaxPayloadKg(),
                drone.getStatus(),
                drone.getLatitude(),
                drone.getLongitude(),
                drone.getCreatedAt(),
                drone.getUpdatedAt());
    }
}
