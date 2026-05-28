package com.shippingontheair.delivery.application.port;

import java.util.List;
import java.util.UUID;

public interface FleetPort {

    List<DroneView> listAvailable();

    void reserve(UUID droneId, double requiredPayloadKg);

    void release(UUID droneId);

    record DroneView(UUID id, String name, double maxPayloadKg, double latitude, double longitude) {}
}
