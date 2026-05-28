package com.shippingontheair.delivery.application.port;

import java.util.UUID;

public interface ShipmentPort {

    ShipmentView getShipment(UUID shipmentId);

    void markDispatched(UUID shipmentId);

    void markDelivered(UUID shipmentId);

    void markFailed(UUID shipmentId, String reason);

    record ShipmentView(
            UUID id,
            String status,
            double weightKg,
            Location origin,
            Location destination) {}

    record Location(String label, double latitude, double longitude) {}
}
