package com.shippingontheair.delivery.application.dispatch;

import com.shippingontheair.delivery.application.port.FleetPort;
import com.shippingontheair.delivery.application.port.ShipmentPort;
import com.shippingontheair.delivery.domain.model.Delivery;
import com.shippingontheair.delivery.domain.model.GeoPoint;
import java.util.Comparator;
import java.util.UUID;

/**
 * Application-level dispatch orchestration (Dispatch bounded context in delivery-service).
 */
public class DispatchOrchestrator {

    private final ShipmentPort shipmentPort;
    private final FleetPort fleetPort;
    private final double speedKmPerHour;

    public DispatchOrchestrator(ShipmentPort shipmentPort, FleetPort fleetPort, double speedKmPerHour) {
        this.shipmentPort = shipmentPort;
        this.fleetPort = fleetPort;
        this.speedKmPerHour = speedKmPerHour;
    }

    public DispatchResult dispatch(UUID shipmentId) {
        ShipmentPort.ShipmentView shipment = shipmentPort.getShipment(shipmentId);
        if (!"REQUESTED".equals(shipment.status())) {
            throw new IllegalStateException("shipment must be REQUESTED, was: " + shipment.status());
        }

        FleetPort.DroneView drone = fleetPort.listAvailable().stream()
                .filter(d -> d.maxPayloadKg() >= shipment.weightKg())
                .min(Comparator.comparingDouble(d -> distance(
                        d.latitude(), d.longitude(),
                        shipment.origin().latitude(), shipment.origin().longitude())))
                .orElseThrow(() -> new DispatchException("no available drone with sufficient capacity"));

        UUID reservedDroneId = null;
        try {
            fleetPort.reserve(drone.id(), shipment.weightKg());
            reservedDroneId = drone.id();

            Delivery delivery = Delivery.start(
                    shipmentId,
                    drone.id(),
                    new GeoPoint(shipment.origin().latitude(), shipment.origin().longitude()),
                    new GeoPoint(shipment.destination().latitude(), shipment.destination().longitude()),
                    speedKmPerHour);

            shipmentPort.markDispatched(shipmentId);
            return new DispatchResult(delivery);
        } catch (RuntimeException ex) {
            if (reservedDroneId != null) {
                try {
                    fleetPort.release(reservedDroneId);
                } catch (RuntimeException ignored) {
                }
            }
            try {
                shipmentPort.markFailed(shipmentId, ex.getMessage());
            } catch (RuntimeException ignored) {
            }
            throw ex instanceof DispatchException ? ex : new DispatchException(ex.getMessage(), ex);
        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        return dLat * dLat + dLon * dLon;
    }

    public record DispatchResult(Delivery delivery) {}
}
