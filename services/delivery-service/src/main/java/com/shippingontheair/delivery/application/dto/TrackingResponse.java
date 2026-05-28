package com.shippingontheair.delivery.application.dto;

import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.DeliveryStatus;
import java.util.UUID;

public record TrackingResponse(
        UUID deliveryId,
        DeliveryStatus phase,
        Position currentPosition,
        Position destination,
        double progressPercent,
        int etaSeconds) {

    public static TrackingResponse from(Delivery delivery) {
        return new TrackingResponse(
                delivery.getId(),
                delivery.getStatus(),
                new Position(delivery.getCurrentPosition().latitude(), delivery.getCurrentPosition().longitude()),
                new Position(delivery.getDestination().latitude(), delivery.getDestination().longitude()),
                delivery.getProgressPercent(),
                delivery.getEtaSeconds());
    }

    public record Position(double latitude, double longitude) {}
}
